// Selecionando os botões e contêineres
const showCreateFormButton = document.getElementById("showCreateForm");
const showAlterFormButton = document.getElementById("showAlterForm");
const showDeleteFormButton = document.getElementById("showDeleteForm");
const showTransferFormButton = document.getElementById("showTransferForm");

const createContainer = document.getElementById("createContainer");
const alterContainer = document.getElementById("alterContainer");
const deleteContainer = document.getElementById("deleteContainer");
const transferContainer = document.getElementById("transferContainer");

const listButton = document.getElementById("listAccounts");
const accountList = document.getElementById("accountList");

const baseURL = "http://localhost:8080/api/contas"; // Base URL do backend

// Função para ocultar todos os contêineres
const hideAllContainers = () => {
  createContainer.classList.add("hidden");
  alterContainer.classList.add("hidden");
  deleteContainer.classList.add("hidden");
  transferContainer.classList.add("hidden");
};

// Alternar para o formulário de criar conta
showCreateFormButton.addEventListener("click", () => {
  hideAllContainers();
  createContainer.classList.remove("hidden");
});

// Alternar para o formulário de alterar conta
showAlterFormButton.addEventListener("click", () => {
  hideAllContainers();
  alterContainer.classList.remove("hidden");
});

// Alternar para o formulário de excluir conta
showDeleteFormButton.addEventListener("click", () => {
  hideAllContainers();
  deleteContainer.classList.remove("hidden");
});

showTransferFormButton.addEventListener("click", () => {
  hideAllContainers();
  transferContainer.classList.remove("hidden");
})

// Função para listar contas
const listarContas = async () => {
  try {
    const response = await fetch(baseURL, { method: "GET" });

    if (!response.ok) {
      throw new Error(`Erro na requisição: ${response.statusText}`);
    }

    const contas = await response.json();

    accountList.innerHTML = "";

    if (contas.length === 0) {
      accountList.innerHTML = "<p>Nenhuma conta encontrada.</p>";
      return;
    }

    contas.forEach((conta) => {
      const contaElement = document.createElement("div");
      contaElement.classList.add("p-2", "rounded-lg", "bg-neutral-700", "mb-2");
      contaElement.innerHTML = `
        <p><strong>Número:</strong> ${conta.numero}</p>
        <p><strong>Saldo:</strong> ${conta.saldo.toFixed(2)}</p>
      `;
      accountList.appendChild(contaElement);
    });
  } catch (err) {
    console.error("Erro ao listar contas", err);
    accountList.innerHTML = "<p>Erro ao carregar as contas. Tente novamente mais tarde.</p>";
  }
};

// Função para cadastrar conta
const cadastrarConta = async (numero, saldo) => {
  try {
    const response = await fetch(baseURL, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({ numero, saldo }),
    });

    if (!response.ok) {
      throw new Error(`Erro na requisição: ${response.statusText}`);
    }

    const data = await response.text();
    console.log(data);
    return true;
  } catch (err) {
    console.error("Erro ao cadastrar conta", err);
    return false;
  }
};

// Função para alterar conta
const alterarConta = async (numero, saldo) => {
  try {
    const response = await fetch(baseURL, {
      method: "PUT",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({ numero, saldo }),
    });

    if (!response.ok) {
      throw new Error(`Erro na requisição: ${response.statusText}`);
    }

    const data = await response.text();
    console.log(data);
    return true;
  } catch (err) {
    console.error("Erro ao alterar conta", err);
    return false;
  }
};

// Função para excluir conta
const excluirConta = async (numero) => {
  try {
    const response = await fetch(`${baseURL}?numero=${numero}`, { method: "DELETE" });

    if (!response.ok) {
      throw new Error(`Erro na requisição: ${response.statusText}`);
    }

    const data = await response.text();
    console.log(data);
    return true;
  } catch (err) {
    console.error("Erro ao excluir conta", err);
    return false;
  }
};

// Função para transferir entre contas
const transferirEntreContas = async (sourceAccount, targetAccount, amount) => {
  try {
    const response = await fetch(`${baseURL}/transferencia`, {
      method: 'POST',
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        numeroOrigem: sourceAccount,
        numeroDestino: targetAccount,
        valor: amount
      })
    });

    if (!response.ok) {
      throw new Error(`Erro na requisição: ${response.statusText}`);
    }

    const data = await response.text();
    console.log(data);
    return true;
  } catch (err) {
    console.error("Erro na transferência", err);
    return false;
  }
}

// Evento para listar contas
listButton.addEventListener("click", listarContas);

// Evento para criar conta
createContainer.querySelector("form").addEventListener("submit", async (event) => {
  event.preventDefault();

  const createConta = document.getElementById("createConta").value;
  const initSaldo = document.getElementById("initSaldo").value;

  const response = await cadastrarConta(createConta, initSaldo);

  if (response) {
    alert(`Conta criada com sucesso!\nNúmero: ${createConta}\nSaldo: ${initSaldo}`);
    listarContas();
  } else {
    alert("Erro ao criar conta. Tente novamente.");
  }
});

// Evento para alterar conta
alterContainer.querySelector("form").addEventListener("submit", async (event) => {
  event.preventDefault();

  const alterConta = document.getElementById("alterConta").value;
  const newSaldo = document.getElementById("newSaldo").value;

  const response = await alterarConta(alterConta, newSaldo);

  if (response) {
    alert(`Conta alterada com sucesso!\nNúmero: ${alterConta}\nNovo Saldo: ${newSaldo}`);
    listarContas();
  } else {
    alert("Erro ao alterar conta. Tente novamente.");
  }
});

// Evento para excluir conta
deleteContainer.querySelector("form").addEventListener("submit", async (event) => {
  event.preventDefault();

  const deleteConta = document.getElementById("deleteConta").value;

  const response = await excluirConta(deleteConta);

  if (response) {
    alert(`Conta ${deleteConta} excluída com sucesso!`);
    listarContas();
  } else {
    alert("Erro ao excluir conta. Tente novamente.");
  }
});

// Evento de submissão do formulário de transferência
transferContainer.querySelector("form").addEventListener("submit", async (event) => {
  event.preventDefault();

  const sourceAccount = document.getElementById("sourceAccount").value;
  const targetAccount = document.getElementById("targetAccount").value;
  const transferAmount = document.getElementById("transferAmount").value;

  const response = await transferirEntreContas(sourceAccount, targetAccount, transferAmount);

  if (response) {
    alert(`Transferência de R$${transferAmount} de ${sourceAccount} para ${targetAccount} realizada com sucesso.`);
    listarContas(); // Atualizar a lista de contas
  } else {
    alert("Erro ao realizar a transferência. Tente novamente.");
  }
});
