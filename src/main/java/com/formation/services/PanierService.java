package com.formation.services;

import com.formation.exceptions.MetierException;
import com.formation.models.Client;
import com.formation.models.Panier;
import com.formation.models.Produit;
import com.formation.models.ProduitAjouteAuPanier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class PanierService {

    public static Panier getPanier(Client client) {

        Panier panier = null;
        try {
            DBService dbService = DBService.getInstance();

            String requete = "SELECT id, date FROM Panier WHERE idClient = ?";
            PreparedStatement preparedStatement = dbService.prepareStatement(requete);
            preparedStatement.setString(1, client.id);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                panier = new Panier();
                panier.id = result.getString("id");
                panier.client = client;
                panier.date = result.getDate("date").toLocalDate();

                // TODO allez chercher les produits du panier

                String requeteProduits = "SELECT idProduit, quantite FROM " +
                        "ProduitAjouteAuPanier WHERE idPanier = ?";
                PreparedStatement preparedStatementProduits = dbService.prepareStatement(requeteProduits);
                preparedStatementProduits.setString(1, panier.id);
                ResultSet resultProduit = preparedStatementProduits.executeQuery();
                while (resultProduit.next()) {
                    Produit produit = ProduitService.getProduit(resultProduit.getString("idProduit"));
                    ProduitAjouteAuPanier produitAjouteAuPanier = new ProduitAjouteAuPanier(produit);
                    produitAjouteAuPanier.quantite = result.getInt("quantite");
                    panier.produits.add(produitAjouteAuPanier);
                }
            }
            result.close();
        } catch (SQLException e) {
            System.err.println("Impossible de se connecter à la base : " + e.getMessage());
        }
        if (panier == null) {
            panier = creerPanier(client);
        }
        return panier;
    }

    private static Panier creerPanier(Client client) {
        Panier panier = new Panier();
        panier.id = UUID.randomUUID().toString();
        panier.client = client;
        panier.produits = new ArrayList<>();
        panier.date = LocalDate.now();
        return panier;
    }

    public static void ajouterProduit(Panier panier, Produit produit) throws MetierException {
        if (panier == null) {
            throw new MetierException("Le panier ne peut etre null");
        }
        if (produit == null) {
            throw new MetierException("Le produit ne peut etre null");
        }
        for (ProduitAjouteAuPanier produitAjouteAuPanier : panier.produits) {
            if (produitAjouteAuPanier.produit.id.equals(produit.id)) {
                produitAjouteAuPanier.quantite++;
                return;
            }

        }

        // TODO : Mettre a jour le ProduitAjouteAuPanier


        panier.produits.add(new ProduitAjouteAuPanier(produit));
    }
}
