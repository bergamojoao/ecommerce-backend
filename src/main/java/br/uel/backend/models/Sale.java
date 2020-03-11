package br.uel.backend.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/*
    STATUS 0: Carrinho aberto...
    STATUS 1: Carrinho Fechado...Aguardando Pagamento...
    STATUS 2: Pagamento Efetuado...Aguardando Confirmacao...
    STATUS 3: Pagamento Confirmado...Aguardando Entrega...
    STATUS 4: Entregue...Pedido Finalizado.
 */


@Entity
public class Sale {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    private int status;
    private String receipt;
    private float total;
    @ManyToOne
    private Client client;
    @ManyToMany
    private List<Product> products = new ArrayList<>();

    public Sale() {
    }

    public Sale(Long id, int status, float total, Client client, List<Product> products) {
        this.id = id;
        this.status = status;
        this.total = total;
        this.client = client;
        this.products = products;
    }

    public void addProduct(Product p){
        this.total+=p.getPrice();
        this.products.add(p);
    }

    public void removeProduct(String codProd){
        for (int i = 0; i <this.products.size(); i++) {
            if(this.products.get(i).getCodProd().compareTo(codProd)==0){
                this.total-=this.products.get(i).getPrice();
                this.products.remove(i);
                break;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }
}
