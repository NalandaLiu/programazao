public class App {

    // Nome do arquivo contendo os dados dos produtos.
    static String nomeArquivoDados;
    
    // Scanner usado para entrada de dados pelo teclado.
    static Scanner teclado;

    // Contador global de produtos lidos do arquivo.
    static int quantosProdutos = 0;

    // Árvore AVL de produtos indexados por ID.
    static ABB<Integer, Produto> produtosPorId;

    // Árvore AVL de produtos indexados por nome/descrição.
    static ABB<String, Produto> produtosPorNome;

    // Tabela hash que relaciona produtos com listas de pedidos.
    static TabelaHash<Produto, Lista<Pedido>> pedidosPorProduto;

    /**
     * Limpa o terminal (funciona para terminais compatíveis com ANSI).
     */
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Pausa a execução esperando que o usuário pressione Enter.
     */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /**
     * Mostra o cabeçalho principal do sistema.
     */
    static void cabecalho() {
        limparTela();
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }

    /**
     * Lê uma opção do usuário convertida para o tipo fornecido.
     * @param mensagem Texto que será exibido para o usuário
     * @param classe Classe do tipo esperado (ex: Integer.class)
     * @return Valor convertido para o tipo especificado, ou null se houver erro
     */
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        T valor;
        System.out.println(mensagem);
        try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (Exception e) {
            return null;
        }
        return valor;
    }

    /**
     * Exibe o menu de opções e retorna a escolha do usuário.
     * @return Opção numérica escolhida.
     */
    static int menu() {
        cabecalho();
        System.out.println("1 - Procurar produtos, por id");
        System.out.println("2 - Recortar produtos, por descrição");
        System.out.println("3 - Pedidos de um produto, em arquivo");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }

    /**
     * Lê os produtos de um arquivo e insere em uma árvore AVL com chave genérica.
     * @param nomeArquivoDados Caminho do arquivo
     * @param extratorDeChave Função que extrai a chave do produto
     * @return Árvore AVL com os produtos lidos
     */
    static <T> ABB<T,Produto> lerProdutos(String nomeArquivoDados,
                                          Function<Produto,T> extratorDeChave) {
        Scanner arquivo = null;
        ABB<T,Produto> produtosCadastrados;

        try {
            arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
            int numProdutos = Integer.parseInt(arquivo.nextLine());
            produtosCadastrados = new AVL<>();

            for (int i = 0; i < numProdutos; i++) {
                String linha = arquivo.nextLine();
                Produto produto = Produto.criarDoTexto(linha);
                T chave = extratorDeChave.apply(produto);
                produtosCadastrados.inserir(chave, produto);
            }

            quantosProdutos = produtosCadastrados.tamanho();

        } catch (IOException e) {
            produtosCadastrados = null;
        } finally {
            if (arquivo != null) arquivo.close();
        }

        return produtosCadastrados;
    }

    /**
     * Localiza um produto na árvore por ID (inserido pelo usuário).
     * Exibe o produto encontrado e suas estatísticas de busca.
     */
    static Produto localizarProdutoID() {
        cabecalho();
        System.out.println("LOCALIZANDO POR ID");
        int ID = lerOpcao("Digite o ID para busca", Integer.class);
        Produto localizado = localizarProduto(produtosPorId, ID);
        mostrarProduto(localizado);
        return localizado;
    }

    /**
     * Pesquisa um produto em uma árvore (por chave genérica).
     * @param produtosCadastrados árvore onde será feita a busca
     * @param chave chave a ser buscada
     * @return produto encontrado (ou null)
     */
    static <K> Produto localizarProduto(ABB<K, Produto> produtosCadastrados, K chave){
        cabecalho();
        Produto localizado = produtosCadastrados.pesquisar(chave);
        System.out.println("Tempo: " + produtosCadastrados.getTempo());
        System.out.println("Comparações: " + produtosCadastrados.getComparacoes());
        pausa();
        return localizado;
    }

    /**
     * Mostra os dados de um produto, ou mensagem de erro se for null.
     * @param produto Produto a ser exibido
     */
    private static void mostrarProduto(Produto produto) {
        cabecalho();
        String mensagem = "Dados inválidos para o produto!";
        if (produto != null) {
            mensagem = String.format("Dados do produto:\n%s", produto);
        }
        System.out.println(mensagem);
    }

    /**
     * Gera uma lista de pedidos aleatórios associando produtos a eles.
     * @param quantidade Quantidade de pedidos a gerar
     * @return Lista de pedidos gerada
     */
    private static Lista<Pedido> gerarPedidos(int quantidade){
        Lista<Pedido> pedidos = new Lista<>();
        Random sorteio = new Random(42); // Semente fixa para repetibilidade
        int quantProdutos;

        for (int i = 0; i < quantidade; i++) {
            Pedido ped = new Pedido();
            quantProdutos = sorteio.nextInt(8)+1;

            for (int j = 0; j < quantProdutos; j++) {
                int id = sorteio.nextInt(7750)+10_000;
                Produto prod = produtosPorId.pesquisar(id);
                ped.incluirProduto(prod);
                inserirNaTabela(prod, ped);
            }

            pedidos.inserir(ped);
        }

        return pedidos;
    }

    /**
     * Insere um pedido em uma tabela hash associada a um produto.
     * Cria nova lista se ainda não houver lista associada ao produto.
     */
    private static void inserirNaTabela(Produto produto, Pedido pedido){
        Lista<Pedido> pedidosDoProduto;
        try {
            pedidosDoProduto = pedidosPorProduto.pesquisar(produto);
        } catch (NoSuchElementException nex) {
            pedidosDoProduto = new Lista<>();
            pedidosPorProduto.inserir(produto, pedidosDoProduto);
        }
        pedidosDoProduto.inserir(pedido);
    }

    /**
     * Recorta e imprime a subárvore entre dois pontos de descrição informados.
     * @param arvore árvore de produtos por descrição
     */
    private static void recortarArvore(ABB<String, Produto> arvore) {
        cabecalho();
        System.out.print("Digite ponto de início do filtro: ");
        String descIni = teclado.nextLine();
        System.out.print("Digite ponto de fim do filtro: ");
        String descFim = teclado.nextLine();
        System.out.println(arvore.recortar(descIni, descFim));
    }

    /**
     * Gera um arquivo com os pedidos de um produto selecionado por ID.
     */
    static void pedidosDoProduto(){
        Produto produto = localizarProdutoID();
        String nomeArquivo = "RelatorioProduto" + produto.hashCode() + ".txt";    

        try (FileWriter arquivoRelatorio = new FileWriter(nomeArquivo)){
            Lista<Pedido> listaProd = pedidosPorProduto.pesquisar(produto);
            arquivoRelatorio.append(listaProd + "\n");
            arquivoRelatorio.close();
            System.out.println("Dados salvos em " + nomeArquivo);
        } catch (IOException e) {
            System.out.println("Problemas para criar o arquivo " + nomeArquivo + ". Tente novamente");
        }
    }

    /**
     * Método principal: inicializa dados, mostra menu e executa ações.
     */
    public static void main(String[] args) {
        teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        nomeArquivoDados = "produtos.txt";

        // Lê produtos indexados por ID, utilizando AVL
        produtosPorId = lerProdutos(nomeArquivoDados, Produto::hashCode);

        // Cria nova árvore AVL indexada por nome, a partir da de IDs
        produtosPorNome = new AVL<>(produtosPorId, prod -> prod.descricao, String::compareTo);

        // Inicializa tabela hash para pedidos
        pedidosPorProduto = new TabelaHash<>((int)(quantosProdutos * 1.25));

        // Gera 25.000 pedidos simulados
        gerarPedidos(25000);

        // Loop de menu
        int opcao = -1;
        do {
            opcao = menu();
            switch (opcao) {
                case 1 -> localizarProdutoID();
                case 2 -> recortarArvore(produtosPorNome);
                case 3 -> pedidosDoProduto();
            }
            pausa();
        } while (opcao != 0);

        teclado.close();
    }
}
