"# programazao" 
+-------------------------------------+
|               BANCO                 |
+-------------------------------------+
| - clientes: List<Cliente>            |
+-------------------------------------+
| + cadastrarCliente(nome, cpf, senha)|
| + obterRelatorio()                  |
| + obterClienteExtremos()            |
| + obterValorCustodia()              |
+-------------------------------------+

               1
               |
               |
               N
+-------------------------------------+
|              CLIENTE                |
+-------------------------------------+
| - nome: str                         |
| - cpf: str                          |
| - senha: str                        |
| - contas: List<Conta>                |
+-------------------------------------+
| + criarConta(tipo: str)             |
| + acessarConta(numero: int)         |
| + obterExtratoMensal()              |
+-------------------------------------+

               1
               |
               |
               N
+-------------------------------------+
|             CONTA (abstract)        |
+-------------------------------------+
| - numero: int                       |
| - saldo: float                      |
| - historico: List<str>              |
+-------------------------------------+
| + depositar(valor: float)           |
| + sacar(valor: float)               |
| + obterExtratoMensal()              |
| + aplicarRendimento()               |
| + cobrarTarifaMensal()              |
+-------------------------------------+
                |
    ------------------------------------
    |            |            |        |
+------------+ +------------+ +-------------+ +----------------+
| CONTA      | | CONTA      | | CONTA       | | CONTA            |
| CORRENTE   | | POUPANCA   | | RENDA FIXA  | | INVESTIMENTO     |
+------------+ +------------+ +-------------+ +-----------------+
| - limite: float | - rendimento: 0.6%      | - imposto: 15%     | - imposto: 22.5%|
| + depositar()   | + aplicarRendimento()   | - rendimento: 0.5-0.85%| - rendimento: -0.6% a 1.5% |
|                 |                        | + aplicarRendimento()  | + aplicarRendimento()      |
|                 |                        | + cobrarTarifaMensal()  | + cobrarTarifaMensal()    |
+-----------------+                        | + calcularImposto()    | + calcularImposto()       |
                                           +-----------------------+ +-------------------------+
