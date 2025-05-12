>>calcularValorMedio: 

public double calcularValorMedio(Function<E, Double> extrator, int quantidade) {
    if (quantidade <= 0 || this.vazia()) {
        return 0.0;
    }

    double soma = 0.0;
    int contador = 0;
    Celula<E> atual = this.primeiro.getProximo(); 

    while (atual != null && contador < quantidade) {
        Double valor = extrator.apply(atual.getItem());
        if (valor != null) {
            soma += valor;
            contador++;
        }
        atual = atual.getProximo();
    }

    if (contador == 0) {
        return 0.0;
    }

    return soma / contador;
}

Esse método calcula a média de um valor extraído de cada elemento da lista (por exemplo, o preço de um produto, a nota de um aluno, etc.).

Parâmetros:
Function<E, Double> extrator: uma função que extrai um valor numérico (Double) de um elemento do tipo E.

int quantidade: indica quantos primeiros elementos da lista devem ser considerados no cálculo da média.

Funcionamento:
Validação: se quantidade <= 0 ou a lista está vazia, retorna 0.0.

Inicialização: começa a leitura a partir do primeiro elemento real da lista (depois da sentinela).

Iteração:

Percorre os primeiros quantidade elementos.

Para cada elemento, aplica a função extrator para obter um valor numérico.

Soma os valores obtidos e conta quantos foram processados.

Resultado:

Se nenhum valor foi processado (por exemplo, todos eram null), retorna 0.0.

Caso contrário, retorna a média aritmética (soma ÷ quantidade processada).



>>Predicate:
import java.util.function.Predicate;

public Lista<E> filtrar(Predicate<E> condicional, int quantidade) {
    if (quantidade <= 0 || this.vazia()) {
        return new Lista<>();
    }

    Lista<E> subLista = new Lista<>();
    Celula<E> atual = this.primeiro.getProximo(); // Ignora a célula sentinela
    int contador = 0;

    while (atual != null && contador < quantidade) {
        E item = atual.getItem();
        if (condicional.test(item)) {
            subLista.inserir(item, subLista.tamanho); // Insere no final da sub-lista
        }
        contador++;
        atual = atual.getProximo();
    }

    return subLista;
}

Parâmetros:
Predicate<E> condicional: uma função que recebe um item do tipo E e retorna true ou false, indicando se o item deve ser incluído ou não.

int quantidade: número máximo de elementos que a sublista pode conter.

Passos do método:
Cria uma nova lista vazia (subLista) para armazenar os elementos que satisfazem a condição.

Começa a percorrer a lista original a partir do primeiro item real (após a sentinela).

Para cada item:

Usa condicional.test(item) para verificar se o item deve ser incluído.

Se sim, insere esse item na subLista, na última posição.

Incrementa o contador.

Interrompe a leitura quando atinge o número máximo (quantidade) ou chega ao final da lista.

Retorna a sublista gerada.

>>Subpilha:
public Pilha<E> subPilha(int numItens) {
    Pilha<E> subPilha = new Pilha<>();
    Celula<E> atual = topo;
    int contador = 0;

    while (atual != fundo && contador < numItens) {
        subPilha.empilhar(atual.getItem());
        atual = atual.getProximo();
        contador++;
    }

    return subPilha;
}

Explicação:
Criação da nova pilha:

Uma nova pilha (subPilha) é criada para armazenar os elementos.
Iteração sobre os elementos da pilha original:

Começa do topo da pilha original e percorre até o fundo ou até atingir o número de itens desejado (numItens).
Empilhamento na nova pilha:

Os itens são empilhados na nova pilha na mesma ordem em que aparecem na pilha original.

Parâmetros:

condicional: Um Predicate<E> que testa se um elemento deve ser incluído na sub-lista.
quantidade: Número máximo de elementos da lista original a serem avaliados.
Validação inicial:

Se quantidade for menor ou igual a 0 ou a lista estiver vazia, retorna uma nova lista vazia.
Iteração:

Percorre os primeiros quantidade elementos da lista original.
Para cada elemento, verifica se ele satisfaz a condição (condicional.test(item)).
Inserção na sub-lista:

Se o elemento passar no teste, ele é inserido no final da sub-lista.
Retorno:

Retorna a sub-lista contendo os elementos que passaram no teste.



>>Exibir Pedidos com Produto:

public static void exibirPedidosComProduto(String nomeProduto, int quantidade) {
    if (pedidosCadastrados.vazia()) {
        System.out.println("Não há pedidos cadastrados.");
        return;
    }

    Lista<Pedido> pedidosFiltrados = pedidosCadastrados.filtrar(
        p -> p.contemProduto(nomeProduto), quantidade
    );
/*Aqui é feita a filtragem da lista original pedidosCadastrados.

É usada a função filtrar(Predicate<E>, int) que você mostrou anteriormente.

p -> p.contemProduto(nomeProduto) é uma expressão lambda que retorna true se o pedido p contiver o produto com o nome especificado.

O resultado é uma nova lista pedidosFiltrados com no máximo quantidade pedidos.

*/

    if (pedidosFiltrados.vazia()) {
        System.out.println("Nenhum pedido encontrado contendo o produto: " + nomeProduto);
    }

/*Se nenhum pedido passou no filtro, imprime uma mensagem dizendo que não encontrou pedidos com o produto.*/

 else {
        System.out.println("Pedidos contendo o produto \"" + nomeProduto + "\":");
        for (int i = 0; i < pedidosFiltrados.tamanho(); i++) {
            System.out.println(pedidosFiltrados.remover(0).resumo());
        }
    }
}


/*Percorre a lista pedidosFiltrados.
A cada iteração:
Remove o primeiro pedido (remover(0));
Chama resumo() no objeto Pedido para imprimir as informações formatadas.
Importante: esse for não está usando o padrão ideal porque tamanho() diminui a cada remover(0). Isso ainda funciona porque a remoção sempre ocorre da frente, mas seria melhor usar um while com checagem de vazia().*/


