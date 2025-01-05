## Intoduction
Cette application repose sur une architecture Java intégrant InfluxDB, Kafka et Grafana pour le traitement et la visualisation des données. Kafka agit comme une plateforme de messagerie centrale, facilitant l’échange de données entre les différents composants, tandis qu’InfluxDB stocke les données temporelles, qui sont ensuite exploitées pour la visualisation dans Grafana.

## Fonctionnalités de l'Application

### 1. Application Kafka Streams
- Lit les transactions financières au format JSON depuis le topic **`payments-queue`**.
- Détecte les transactions suspectes avec des montants supérieurs à **15 000**.
- Publie les transactions suspectes dans le topic **`alerts-stream`**.

### 2. Stockage en Temps Réel
- Les transactions suspectes sont enregistrées dans la base de données **InfluxDB** pour un suivi en temps réel.

### 3. Tableau de Bord Interactif
- **Grafana** est utilisé pour afficher les données en temps réel avec les métriques suivantes :
  - **Nombre total de transactions suspectes.**
  - **Montant de paiement le plus élevé.**
  - **Somme des montants des paiements frauduleux par utilisateur.**
 ## Architecture
 ![image](https://github.com/user-attachments/assets/67a6cac4-e450-4b09-b9f5-0cbb3b719623)
 ## Démonstration
 https://github.com/user-attachments/assets/15a365b2-db96-4010-a8c0-14f70721e413

