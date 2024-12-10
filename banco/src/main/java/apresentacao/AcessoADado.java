package apresentacao;

import java.util.*;

import javax.sql.*;

import java.sql.*;

import org.springframework.stereotype.Component;

@Component
public class AcessoADado {

  private final DataSource dataSource;

  public AcessoADado(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Connection connect() throws SQLException {
    return dataSource.getConnection();
  }

  /**
   * Cadastra uma nova conta no banco de dados.
   * 
   * @param numero número de conta
   * @param saldo saldo inicial da conta
   * @return mensagem de sucesso ou erro
   */
  
   public String cadastrarConta(String numero, float saldo) {
    String SQL = "INSERT INTO public.conta(numero, saldo) values (?, ?)";
    String mensagem;

    try(Connection conn = connect();
        PreparedStatement pstmt = conn.prepareStatement(SQL)) {
          pstmt.setString(1, numero); // Corrigido: tipo String para o número
          pstmt.setFloat(2, saldo); // Corrigido: tipo Float para o saldo

          int affectedRows = pstmt.executeUpdate();
          if(affectedRows > 0) {
            mensagem = "Cadastro de conta " + numero + " realizado com sucesso.";
          } else {
            mensagem = "Erro ao cadastrar conta.";
          }
    } catch(SQLException ex) {
      mensagem = "Erro de SQL: " + ex.getMessage();
    }

    return mensagem;
   }

   /**
     * Altera o saldo de uma conta.
     *
     * @param numero número da conta
     * @param saldo  novo saldo
     * @return mensagem de sucesso ou erro
    */

    public String alterarConta(String numero, float saldo) {
      String SQL = "UPDATE public.conta SET saldo = ? WHERE numero = ?";
      String mensagem;

      try (Connection conn = connect();
          PreparedStatement pstmt = conn.prepareStatement(SQL)) {

          pstmt.setFloat(1, saldo);  // Corrigido: tipo Float para o saldo
          pstmt.setString(2, numero);

          int affectedRows = pstmt.executeUpdate();
          if (affectedRows > 0) {
              mensagem = "Saldo da conta " + numero + " alterado com sucesso.";
          } else {
              mensagem = "Conta não encontrada.";
          }
      } catch (SQLException ex) {
          mensagem = "Erro de SQL: " + ex.getMessage();
      }

      return mensagem;
    }

   /**
    * Lista todas as contas de banco de dados.
    */
    public List<Map<String, Object>> listarContas() {
      String SQL = "SELECT numero, saldo FROM public.conta";
      List<Map<String, Object>> contas = new ArrayList<>();

      try(Connection conn = connect();
          PreparedStatement pstmt = conn.prepareStatement(SQL);
          ResultSet rs = pstmt.executeQuery()) {
            while(rs.next()) {
              Map<String, Object> conta = new HashMap<>();
              conta.put("numero", rs.getString("numero"));
              conta.put("saldo", rs.getFloat("saldo"));
              contas.add(conta);
            }
      } catch(SQLException ex) {
        System.out.println("Erro de SQL: " + ex.getMessage());
      }

      return contas;
    }

    /**
     * Exclui uma conta do banco de dados.
     * 
     * @param numero número da conta
     * @return mensagem de sucesso ou erro
     */

    public String excluirConta(String numero) {
      String SQL = "DELETE FROM public.conta WHERE numero = ?";
      String mensagem;

      try(Connection conn = connect();
        PreparedStatement pstmt = conn.prepareStatement(SQL)) {
          pstmt.setString(1, numero);

          int affectedRows = pstmt.executeUpdate();
          if(affectedRows > 0) {
            mensagem = "Conta " + numero + " excluída com sucesso.";
          } else {
            mensagem = "Conta não encontrada.";
          }
      } catch(SQLException ex) {
        mensagem = "Erro de SQL: " + ex.getMessage();
      }

      return mensagem;
    }

    /**
     * Transfere saldo em uma conta para outra.
     * 
     * @param numeroOrigem número da conta de origim
     * @param numeroDestino número da conta de destino
     * @param valor valor a ser transferido
     * @return mensagem de sucesso ou erro
     */
    public String transferirSaldo(String numeroOrigem, String numeroDestino, float valor) {
      String debitarSQL = "UPDATE public.conta SET saldo = saldo - ? WHERE numero = ?";
      String creditarSQL = "UPDATE public.conta SET saldo = saldo + ? WHERE numero = ?";
      String mensagem;

      try(Connection conn = connect()) {
        conn.setAutoCommit(false); // Inicia a transação

        try (
          PreparedStatement debitarStmt = conn.prepareStatement(debitarSQL);
          PreparedStatement creditarStmt = conn.prepareStatement(creditarSQL);
        ) {
          // Debita o valor da conta de origem
          debitarStmt.setFloat(1, valor);
          debitarStmt.setString(2, numeroOrigem);
          int debitoAfetado = debitarStmt.executeUpdate();

          if(debitoAfetado == 0) {
            throw new SQLException("Conta de origem não encontrada ou saldo insuficiente");
          }

          // credita o valor na conta de destino
          creditarStmt.setFloat(1, valor);
          creditarStmt.setString(2, numeroDestino);
          int creditoAfetado = creditarStmt.executeUpdate();

          if(creditoAfetado == 0) {
            throw new SQLException("Conta de destino não encontrada");
          }

          conn.commit(); // confirma a transação
          mensagem = "Transferência de R$" + valor + " de " + numeroOrigem + " para " + numeroDestino + " realizada com sucesso.";
        } catch(SQLException ex) {
          conn.rollback(); // Reverte a transação em caso de erro
          mensagem = "Erro durante a transferência: " + ex.getMessage();
        }
      } catch(SQLException ex) {
        mensagem = "Erro de conexão ou SQL: " + ex.getMessage();
      }

      return mensagem;
    }
}