import java.util.function.Function;
import java.util.function.Predicate;

public class Lista<E> {

	private Celula<E> primeiro;
	private Celula<E> ultimo;
	private int tamanho;
	
	public Lista() {
		
		Celula<E> sentinela = new Celula<>();
		
		this.primeiro = this.ultimo = sentinela;
		this.tamanho = 0;
	}
	
	public boolean vazia() {
		
		return (this.primeiro == this.ultimo);
	}
	
	public void inserir(E novo, int posicao) {
		
		Celula<E> anterior, novaCelula, proximaCelula;
		
		if ((posicao < 0) || (posicao > this.tamanho))
			throw new IndexOutOfBoundsException("Não foi possível inserir o item na lista: "
					+ "a posição informada é inválida!");
		
		anterior = this.primeiro;
		for (int i = 0; i < posicao; i++)
			anterior = anterior.getProximo();
				
		novaCelula = new Celula<>(novo);
			
		proximaCelula = anterior.getProximo();
			
		anterior.setProximo(novaCelula);
		novaCelula.setProximo(proximaCelula);
			
		if (posicao == this.tamanho)  // a inserção ocorreu na última posição da lista
			this.ultimo = novaCelula;
			
		this.tamanho++;		
	}
	
	public E remover(int posicao) {
		
		Celula<E> anterior, celulaRemovida, proximaCelula;
		
		if (vazia())
			throw new IllegalStateException("Não foi possível remover o item da lista: "
					+ "a lista está vazia!");
		
		if ((posicao < 0) || (posicao >= this.tamanho ))
			throw new IndexOutOfBoundsException("Não foi possível remover o item da lista: "
					+ "a posição informada é inválida!");
			
		anterior = this.primeiro;
		for (int i = 0; i < posicao; i++)
			anterior = anterior.getProximo();
				
		celulaRemovida = anterior.getProximo();
				
		proximaCelula = celulaRemovida.getProximo();
				
		anterior.setProximo(proximaCelula);
		celulaRemovida.setProximo(null);
				
		if (celulaRemovida == this.ultimo)
			this.ultimo = anterior;
				
		this.tamanho--;
				
		return (celulaRemovida.getItem());	
	}

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
public Lista<E> filtrar(Predicate<E> condicional, int quantidade) {
    if (quantidade <= 0 || this.vazia()) {
        return new Lista<>();
    }

    Lista<E> subLista = new Lista<>();
    Celula<E> atual = this.primeiro.getProximo(); 
    int contador = 0;

    while (atual != null && contador < quantidade) {
        E item = atual.getItem();
        if (condicional.test(item)) {
            subLista.inserir(item, subLista.tamanho); 
        contador++;
        atual = atual.getProximo();
    }

    return subLista;
}

}
}