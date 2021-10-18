package aspirateur;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Telechargement extends Thread implements Runnable{
    protected URL url;
    protected String nom, dossier;
    protected long taille, donnees;
    protected double pourcentage;
    protected Statut statut;
    protected boolean verification;
    protected static final int TAILLE_MAX = 1024;// un byte pour le telechargement
    public Telechargement(String  url,String nDossier ) throws MalformedURLException {
        this.url = verifieUrl(url);
        this.taille = -1;
        this.donnees = 0;
        this.statut = Statut.TELECHARGEMENT;
        this.dossier = nDossier;
        commencer();
    }
    public Telechargement(String url) throws MalformedURLException {
        this.url = verifieUrl(url);
        this.taille = -1;
        this.donnees = 0;
        this.statut = Statut.TELECHARGEMENT;
        this.dossier = "";
        commencer();
    }

    public void setNom(String s){nom = s;}
    public void changerStatut(Statut s){statut = s;}
    public void echec(){changerStatut(Statut.ECHEC);}
    public void pause(){changerStatut(Statut.PAUSE);}
    public void continuer(){
        changerStatut(Statut.TELECHARGEMENT);
        commencer();
    }
    public void annuler(){changerStatut(Statut.ANNULE);}

    // verification de L'Url
    public URL verifieUrl(String url) throws MalformedURLException {
        URL temp = null;
        if(url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://")){
            System.out.println("verification");
            try{
                temp = new URL(url);
                String tempStr = temp.getPath();
                setNom(tempStr.substring(tempStr.lastIndexOf('/')+1, tempStr.length())) ;
                System.out.println(nom);
                verification = true;
                System.out.println("URL correct");
            }catch (Exception ex  ){
                System.out.println("Erreur");
            }
        }
        return temp;
    }
    public void commencer(){
        if(verification){
            System.out.println("Lancer");
            Thread t = new Thread(this);
            t.start();
        }else{
            echec();
        }
    }

    public void run() {
        RandomAccessFile tempFile = null;
        InputStream tempStream = null;
        int speed = 0;
        try {
            //Ouvrez la connexion à l'URL.
            HttpURLConnection tempCon = (HttpURLConnection) url.openConnection();
            tempCon.setConnectTimeout(1000);
            //Spécifiez la partie du fichier à télécharger
            tempCon.setRequestProperty("Range", "bytes=" + donnees + "-");
            //Connecter au serveur.
            tempCon.connect();
            // Assurez-vous que le code de réponse est dans la plage 200.
            if((tempCon.getResponseCode() / 100) != 2) echec();
            int tempLength = tempCon.getContentLength();
            //Vérifiez la longueur du contenu valide.
            if(tempLength < 1) echec();
            else{taille = tempLength;}
            if(dossier.length() > 0 ) dossier += "/";
            String tempPath = dossier + nom;
            System.out.println("Nom :" + nom);
            tempFile = new RandomAccessFile(tempPath, "rw");
            tempFile.seek(donnees);
            tempStream = tempCon.getInputStream();
            while(statut == Statut.TELECHARGEMENT){
		/* Taille du tampon en fonction de la quantité de
		   le fichier est laissé à télécharger*/
                byte[] data;
                if(taille - donnees > TAILLE_MAX){
                    data = new byte[TAILLE_MAX];
                }else{
                    data = new byte[(int)(taille - donnees)];
                }
                //Lire du serveur dans le tampon
                int tempData = tempStream.read(data);
                if(tempData == -1) break;
                // Ecrire le tampon dans un fichier
                tempFile.write(data,0, tempData);
                donnees += tempData;
                pourcentage = ((double) donnees / (double)taille) ;
                changerStatut(Statut.TELECHARGEMENT);
            }
	    /*Changer le statut pour terminer si ce point était
	      atteint car le téléchargement est terminé*/
            if(statut == Statut.TELECHARGEMENT){
                changerStatut(Statut.TERMINE);
            }
        }catch (Exception e){
            echec();
        }finally {
            // fermer le file.
            if (tempFile != null) {
                try {
                    tempFile.close();
                } catch (Exception e) {
                    echec();
                }
            }
            //fermer la connection
            if (tempStream != null) {
                try {
                    tempStream.close();
                } catch (Exception e) {}
            }
        }
    }
    static enum Statut{
        ANNULE,
        PAUSE,
        TERMINE,
        TELECHARGEMENT,
        ECHEC;
    }
}