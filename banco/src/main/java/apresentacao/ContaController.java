package apresentacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    private final AcessoADado acessoADado;

    @Autowired
    public ContaController(AcessoADado acessoADado) {
        this.acessoADado = acessoADado;
    }

    @PostMapping
    public String cadastrarConta(@RequestParam String numero, @RequestParam float saldo) {
        return acessoADado.cadastrarConta(numero, saldo);
    }

    @PutMapping
    public String alterarConta(@RequestParam String numero, @RequestParam float saldo) {
        return acessoADado.alterarConta(numero, saldo);
    }

    @GetMapping
    public List<Map<String, Object>> listarContas() {
        return acessoADado.listarContas();
    }

    @DeleteMapping
    public String excluirConta(@RequestParam String numero) {
        return acessoADado.excluirConta(numero);
    }

    @PostMapping("/transferencia")
    public ResponseEntity<String> transferirSaldo(
            @RequestBody Map<String, Object> transferencia) {
        try {
            String numeroOrigem = (String) transferencia.get("numeroOrigem");
            String numeroDestino = (String) transferencia.get("numeroDestino");
            float valor = Float.parseFloat(transferencia.get("valor").toString());

            String mensagem = acessoADado.transferirSaldo(numeroOrigem, numeroDestino, valor);
            return ResponseEntity.ok(mensagem); // Resposta de sucesso
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro na transferência: " + e.getMessage());
        }
    }
}
