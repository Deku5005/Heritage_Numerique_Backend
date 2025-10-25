package com.heritage.entite;

/**
 * Enumération des langues supportées pour la traduction des contenus.
 * 
 * Utilisée par le système de traduction HuggingFace NLLB-200
 * pour définir les langues disponibles pour la traduction des contenus familiaux.
 */
public enum Langue {
    
    /**
     * Français - Langue principale du projet Heritage Numérique
     * Code ISO 639-1 : fr
     */
    FR("fr", "Français", "Français"),
    
    /**
     * Anglais - Langue internationale
     * Code ISO 639-1 : en
     */
    EN("en", "English", "Anglais"),
    
    /**
     * Bambara - Langue locale du Mali et de l'Afrique de l'Ouest
     * Code ISO 639-1 : bm
     * Langue maternelle de nombreuses familles africaines
     */
    BM("bm", "Bamanankan", "Bambara");
    
    private final String code;
    private final String nomNatif;
    private final String nomFrancais;
    
    /**
     * Constructeur privé pour l'enum Langue.
     * 
     * @param code Code ISO 639-1 de la langue (2-3 caractères)
     * @param nomNatif Nom de la langue dans sa propre langue
     * @param nomFrancais Nom de la langue en français
     */
    Langue(String code, String nomNatif, String nomFrancais) {
        this.code = code;
        this.nomNatif = nomNatif;
        this.nomFrancais = nomFrancais;
    }
    
    /**
     * Retourne le code ISO 639-1 de la langue.
     * 
     * @return Code de la langue (ex: "fr", "en", "bm")
     */
    public String getCode() {
        return code;
    }
    
    /**
     * Retourne le nom de la langue dans sa propre langue.
     * 
     * @return Nom natif (ex: "Français", "English", "Bamanankan")
     */
    public String getNomNatif() {
        return nomNatif;
    }
    
    /**
     * Retourne le nom de la langue en français.
     * 
     * @return Nom en français (ex: "Français", "Anglais", "Bambara")
     */
    public String getNomFrancais() {
        return nomFrancais;
    }
    
    /**
     * Retourne la langue par défaut du système.
     * 
     * @return FR (Français) comme langue par défaut
     */
    public static Langue getDefault() {
        return FR;
    }
    
    /**
     * Trouve une langue par son code ISO 639-1.
     * 
     * @param code Code de la langue à rechercher
     * @return Langue correspondante ou null si non trouvée
     */
    public static Langue findByCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (Langue langue : values()) {
            if (langue.getCode().equalsIgnoreCase(code)) {
                return langue;
            }
        }
        return null;
    }
    
    /**
     * Vérifie si le code fourni correspond à une langue supportée.
     * 
     * @param code Code à vérifier
     * @return true si la langue est supportée
     */
    public static boolean isSupported(String code) {
        return findByCode(code) != null;
    }
    
    /**
     * Retourne toutes les langues supportées sous forme de codes.
     * 
     * @return Tableau des codes de langues supportées
     */
    public static String[] getAllCodes() {
        Langue[] langues = values();
        String[] codes = new String[langues.length];
        for (int i = 0; i < langues.length; i++) {
            codes[i] = langues[i].getCode();
        }
        return codes;
    }
    
    /**
     * Représentation textuelle de la langue.
     * 
     * @return Format : "Code - NomNatif (NomFrancais)"
     */
    @Override
    public String toString() {
        return String.format("%s - %s (%s)", code, nomNatif, nomFrancais);
    }
}
