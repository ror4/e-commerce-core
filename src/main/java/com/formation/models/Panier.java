package com.formation.models;

import java.time.LocalDate;
import java.util.List;

public class Panier {

    public String id;
    public Client client;
    public List<ProduitAjouteAuPanier> produits;
    public LocalDate date;

}

