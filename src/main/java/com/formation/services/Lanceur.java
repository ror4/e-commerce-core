package com.formation.services;

import com.formation.exceptions.MetierException;
import com.formation.models.Client;
import com.formation.models.Panier;
import com.formation.models.Produit;
import com.formation.models.ProduitAjouteAuPanier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Lanceur {

    public static void main(String[] args) {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {

            afficheMenu();

            try {
                String command = br.readLine();
                if (command.equals("0")) {

                    exit();

                } else if (command.equals("1")) {

                    creerProduit(br);

                } else if (command.equals("2")) {

                    listerProduit();

                } else if (command.equals("3")) {

                    creerClient(br);

                } else if (command.equals("4")) {

                    listerClient();

                } else if (command.equals("5")) {

                    ajouteAuPanier(br);

                } else if (command.equals("6")) {

                    detailClient(br);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void detailClient(BufferedReader br) throws IOException {
        System.out.print("Veuillez saisir l'id du client : ");
        String idClient = br.readLine();

        Client client = ClientService.getClient(idClient);
        Panier panier = PanierService.getPanier(client);
        System.out.println(String.format("%s / %s / %s / %s", client.id, client.email, client.nom, client.prenom));
        for (ProduitAjouteAuPanier produitAjouteAuPanier : panier.produits) {
            System.out.println(String.format("%s / %s / %.2f€ / %d", produitAjouteAuPanier.produit.id, produitAjouteAuPanier.produit.nom, produitAjouteAuPanier.produit.prixUnitaire, produitAjouteAuPanier.quantite));
        }
    }

    private static void ajouteAuPanier(BufferedReader br) throws IOException {
        System.out.print("Veuillez saisir l'id du client : ");
        String idClient = br.readLine();
        System.out.print("Veuillez saisir l'id du produit : ");
        String idProduit = br.readLine();

        try {
            Client client = ClientService.getClient(idClient);
            Panier panier = PanierService.getPanier(client);
            Produit produit = ProduitService.getProduit(idProduit);
            PanierService.ajouterProduit(panier, produit);
        } catch (MetierException e) {
            System.out.println("Impossible d'ajouter le produit au panier : " + e.getMessage());
        }
    }

    private static void listerClient() {
        List<Client> clients = ClientService.lister();
        for (Client client : clients) {
            System.out.println(String.format("%s / %s / %s / %s", client.id, client.email, client.nom, client.prenom));
        }
    }

    private static void creerClient(BufferedReader br) throws IOException {
        System.out.println("Veuillez saisir les données du Client");
        System.out.print("Email : ");
        String email = br.readLine();
        System.out.print("Nom : ");
        String nom = br.readLine();
        System.out.print("Prenom : ");
        String prenom = br.readLine();
        try {
            Client client = ClientService.creerClient(email, nom, prenom);
            ClientService.enregistrer(client);
            System.out.println("Le client a été créé avec succès");
        } catch (MetierException e) {
            System.out.println("Désolé, " + e.getMessage() + ", recommence");
        }
    }

    private static void listerProduit() {
        List<Produit> produits = ProduitService.lister();
        if (produits == null || produits.isEmpty()) {
            System.out.println("Aucun produit");
        } else {
            for (Produit produit : produits) {
                System.out.println(String.format("%s / %s / %.2f€", produit.id, produit.nom, produit.prixUnitaire));
            }
        }
    }

    private static void creerProduit(BufferedReader br) throws IOException {
        System.out.println("Veuillez saisir les données du Produit");
        System.out.print("Nom : ");
        String nom = br.readLine();
        System.out.print("Prix Unitaire : ");
        String strPrixUnitaire = br.readLine();

        Float prixUnitaire = null;
        try {
            prixUnitaire = Float.parseFloat(strPrixUnitaire);

            try {
                Produit produit = ProduitService.creerProduit(nom, prixUnitaire);
                ProduitService.enregistrer(produit);
            } catch (MetierException e) {
                System.out.println("Désolé, " + e.getMessage() + ", recommence");
            }

        } catch (NumberFormatException e) {
            System.out.println("Veuillez saisir un prix unitaire correct");
        }
    }

    private static void exit() {
        System.out.println("Au revoir");
        DBService.getInstance().close();
        System.exit(0);
    }

    private static void afficheMenu() {
        System.out.println("Menu:");
        System.out.println("[0] : exit");
        System.out.println("[1] : créer un Produit");
        System.out.println("[2] : lister les Produits");
        System.out.println("[3] : créer un Client");
        System.out.println("[4] : lister les Clients");
        System.out.println("[5] : Ajouter un produit au panier");
        System.out.println("[6] : Détail d'un client");
        System.out.print("Votre choix : ");
    }
}
