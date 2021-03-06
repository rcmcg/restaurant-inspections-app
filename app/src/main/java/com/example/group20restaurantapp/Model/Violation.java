package com.example.group20restaurantapp.Model;

import android.util.Log;

import com.example.group20restaurantapp.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Contains information about a particular violation
 */

public class Violation implements Serializable {
    private int violNumber;
    private Boolean critical;
    private String violDetails;
    private String briefDetails;
    private Boolean repeat;
    private int violImgId;

    // Constructor
    public Violation(int violNumber, Boolean critical, String violDetails, String briefDetails, Boolean repeat) {
        this.violNumber = violNumber;
        this.critical = critical;
        this.violDetails = violDetails;
        this.briefDetails = briefDetails;
        this.repeat = repeat;
        setViolImgId(violNumber);
    }

    public int getViolImgId() {
        return violImgId;
    }

    // Sets image of violation depending on what the violation is
    public void setViolImgId(int violNumber) {
        // Create collection of permit related violations
        List<Integer> permitViolNums = Arrays.asList(103, 104, 212, 314, 501, 502);
        ArrayList<Integer> permitViolArrayList = new ArrayList<>();
        permitViolArrayList.addAll(permitViolNums);

        // Create collection of bad food related violations
        List<Integer> badFoodViolNums = Arrays.asList(201,202,203,204,205,206,208,209,210,211);
        ArrayList<Integer> badFoodArrayList = new ArrayList<>();
        badFoodArrayList.addAll(badFoodViolNums);

        // Create collection of utensil/equipment related violations
        List<Integer> utensilsViolNums = Arrays.asList(301,302,303,307,308,310);
        ArrayList<Integer> utensilsArrayList = new ArrayList<>();
        utensilsArrayList.addAll(utensilsViolNums);

        if (violNumber == 101) {
            this.violImgId = R.drawable.violation_construction;
        } else if (violNumber == 102) {
            this.violImgId = R.drawable.violation_restaurant;
        } else if (permitViolArrayList.contains(violNumber)) {
            this.violImgId = R.drawable.violation_permit;
        } else if (badFoodArrayList.contains(violNumber)) {
            this.violImgId = R.drawable.violation_bad_food;
        } else if (utensilsArrayList.contains(violNumber)) {
            this.violImgId = R.drawable.violation_utensils;
        } else if (violNumber == 304 || violNumber == 305) {
            this.violImgId = R.drawable.violation_rat;
        } else if (violNumber == 306 || violNumber == 311) {
            this.violImgId = R.drawable.violation_dirty_kitchen;
        } else if (violNumber == 309) {
            this.violImgId = R.drawable.violation_cleaners;
        } else if (violNumber == 312) {
            this.violImgId = R.drawable.violation_storage;
        } else if (violNumber == 313) {
            this.violImgId = R.drawable.violation_dog;
        } else if (violNumber == 315) {
            this.violImgId = R.drawable.violation_thermometer;
        } else if (violNumber == 401 || violNumber == 402 || violNumber == 403) {
            this.violImgId = R.drawable.violation_washing_hands;
        } else if (violNumber == 404) {
            this.violImgId = R.drawable.violation_smoking;
        } else {
            this.violImgId = R.drawable.violation_generic;
        }
    }

    public int getViolNumber() {
        return violNumber;
    }

    public Boolean getCritical() {
        return critical;
    }

    //Returns the violation details, The details are translated to French if the device language is french
    //Returns spanish if the device language is spanish
    public String getViolDetails() {
        if(Locale.getDefault().getLanguage()=="fr"){
            if(this.getViolNumber()==201){
                return"Aliments contaminés ou impropres à la consommation humaine [art. 13]";
            }else if(this.getViolNumber()==202){
                return"Food non transformé en manière qui rend la consommation sans danger [par. 14 (1)]";
            }else if(this.getViolNumber()==203){
                return"Aliments non refroidis de manière acceptable [art. 12 (a)]";
            }else if(this.getViolNumber()==205){
                return"Cold Food potentiellement dangereux stocké / affiché au-dessus de 4 ° C. [S. 14 (2)]";
            }else if(this.getViolNumber()==206){
                return"Aliments chauds potentiellement dangereux stockés / affichés en dessous de 60 ° C. [S. 14 (2)]";
            }else if(this.getViolNumber()==209){
                return"Aliments non protégés contre la contamination [par. 12 (a)]";
            }else if(this.getViolNumber()==210){
                return"Food not décongelé de manière acceptable [par. 14 (2)]";
            }else if(this.getViolNumber()==211){
                return"Congelé aliments potentiellement dangereux entreposés / exposés au-dessus de -18 ° C. [par. 14 (3)]";
            }else if(this.getViolNumber()==301){
                return"Équipement / ustensiles / surfaces en contact avec les aliments non maintenus dans des conditions sanitaires [par. 17 (1)]";
            }else if(this.getViolNumber()==302){
                return"Équipement / ustensiles / surfaces en contact avec les aliments mal lavés et désinfectés [par. 17 (2)]";
            }else if(this.getViolNumber()==304){
                return"Locaux non exempts de parasites [par. 26 (a)]";
            }else if(this.getViolNumber()==305){
                return"Conditions observées pouvant permettre l'entrée / l'hébergement / la reproduction d'organismes nuisibles [al. 26 (b) (c)]";
            }else if(this.getViolNumber()==306){
                return"Locaux destinés aux aliments non maintenu dans un état sanitaire [art. 17 (1)]";
            }else if(this.getViolNumber()==308){
                return"L'équipement / les ustensiles / les surfaces en contact avec les aliments ne sont pas en bon état de fonctionnement [art. 16 (b)]";
            }else if(this.getViolNumber()==309){
                return"Nettoyants chimiques et agents similaires entreposés ou étiquetés incorrectement [art. 27]";
            }else if(this.getViolNumber()==315){
                return"Les unités de réfrigération et l'équipement de maintien au chaud manquent de thermomètres précis [par. 19 (2)]";
            }else if(this.getViolNumber()==401){
                return" Stations de lavage des mains adéquates non disponibles pour les employés [par. 21 (4)]";
            }else if(this.getViolNumber()==402){
                return"L'employé ne se lave pas les mains correctement ou à une fréquence adéquate [par. 21 (3)]";
            }else if(this.getViolNumber()==501){
                return"l'exploitant n'a pas le niveau FOODSAFE 1 ou l'équivalent [par. 10 (1)]";
            }else{
                return "violation générique";
            }
        } else if(Locale.getDefault().getLanguage()=="es") {
            if (this.getViolNumber () == 201) {
                return "Alimentos contaminados o no aptos para el consumo humano [art. 13]";
            } else if (this.getViolNumber () == 202) {
                return "Los alimentos no procesados ​​de manera que sea seguro consumirlos [s. 14 (1)]";
            } else if (this.getViolNumber () == 203) {
                return "Alimentos no enfriados de manera aceptable [art. 12 (a)]";
            } else if (this.getViolNumber () == 205) {
                return "Alimentos fríos potencialmente peligrosos almacenados / exhibidos por encima de 4 ° C. [S. 14 (2)]";
            } else if (this.getViolNumber () == 206) {
                return "Alimentos calientes potencialmente peligrosos almacenados / exhibidos por debajo de 60 ° C [S. 14 (2)]";
            } else if (this.getViolNumber () == 209) {
                return "Alimentos no protegidos de la contaminación [párr. 12 (a)]";
            } else if (this.getViolNumber () == 210) {
                return "Alimentos no descongelados de manera aceptable [sección 14 (2)]";
            } else if (this.getViolNumber () == 211) {
                return"Alimentos congelados potencialmente peligrosos almacenados / exhibidos por encima de -18 ° C [s. 14 (3)]";
            } else if (this.getViolNumber () == 301) {
                return "Equipo / utensilios / superficies en contacto con alimentos que no se mantienen bajo condiciones sanitarias [s. 17 (1)]";
            } else if (this.getViolNumber () == 302) {
                return "Equipo / utensilios / superficies en contacto con alimentos lavados y desinfectados de manera inadecuada [s. 17 (2)]";
            } else if (this.getViolNumber () == 304) {
                return "Locales libres de plagas [párr. 26 (a)]";
            } else if (this.getViolNumber () == 305) {
                return "Condiciones observadas que pueden permitir la entrada / albergue / reproducción de organismos nocivos [párrafo 26 (b) (c)]";
            } else if (this.getViolNumber () == 306) {
                return "Locales de alimentos no mantenidos en un estado sanitario [art. 17 (1)]";
            } else if (this.getViolNumber () == 308) {
                return "El equipo / utensilios / superficies en contacto con los alimentos no funcionan correctamente [art. 16 (b)]";
            } else if (this.getViolNumber () == 309) {
                return "Limpiadores químicos y agentes similares almacenados o etiquetados incorrectamente [art. 27]";
            } else if (this.getViolNumber () == 315) {
                return "Las unidades de refrigeración y los equipos de calentamiento carecen de termómetros precisos [sección 19 (2)]";
            } else if (this.getViolNumber () == 401) {
                return "Estaciones de lavado de manos adecuadas no disponibles para empleados [s. 21 (4)]";
            } else if (this.getViolNumber () == 402) {
                return "El empleado no se lava las manos adecuadamente o con la frecuencia adecuada [s. 21 (3)]";
            } else if (this.getViolNumber () == 501) {
                return "el operador no tiene el nivel 1 FOODSAFE o el equivalente [sección 10 (1)]";
            } else {
                return "violación genérica";
            }
        } else {
            return violDetails;
        }
    }

    // Returns brief details of the violation, used in Toast messages if the user presses any violation for details
    // returns in French if the device language is french
    // returns spanish if the device language is spanish
    public String getBriefDetails() {
        if (Locale.getDefault().getLanguage() == "fr") {
            if (this.getViolNumber() == 101) {
                return "Plans de construction ignorant les règlements";
            } else if (this.getViolNumber() == 102) {
                return "prémisse non approuvée";
            } else if (this.getViolNumber() == 103) {
                return "Permis invalide";
            } else if (this.getViolNumber() == 104) {
                return "permis caché";
            } else if (this.getViolNumber() == 201) {
                return "aliments contaminés";
            } else if (this.getViolNumber() == 202) {
                return "Aliments mal transformés";
            } else if (this.getViolNumber() == 203) {
                return "les aliments ne sont pas refroidis correctement";
            } else if (this.getViolNumber() == 204) {
                return "La nourriture n'est pas cuite correctement";
            } else if (this.getViolNumber() == 205) {
                return "aliments froids mal stockés";
            } else if (this.getViolNumber() == 206) {
                return "aliments chauds mal stockés";
            } else if (this.getViolNumber() == 208) {
                return "Aliments obtenus d'une source non approuvée";
            } else if (this.getViolNumber() == 209) {
                return "Aliments non protégés contre la contamination";
            } else if (this.getViolNumber() == 210) {
                return "nourriture non décongelée correctement";
            } else if (this.getViolNumber() == 211) {
                return "les aliments surgelés ne sont pas stockés correctement";
            } else if (this.getViolNumber() == 212) {
                return "Mauvaises procédures écrites de manipulation des aliments";
            } else if (this.getViolNumber() == 301) {
                return "Surfaces et équipements de cuisine sales";
            } else if (this.getViolNumber() == 302) {
                return "Surfaces et équipements de cuisine sales";
            } else if (this.getViolNumber() == 303) {
                return "Mauvais équipement ou installations sanitaires";
            } else if (this.getViolNumber() == 304) {
                return "Ravageurs trouvés sur place";
            } else if (this.getViolNumber() == 305) {
                return "mauvaise prévention des ravageurs";
            } else if (this.getViolNumber() == 306) {
                return "local insalubre";
            } else if (this.getViolNumber() == 307) {
                return "Matériel ou ustensiles de mauvaise qualité";
            } else if (this.getViolNumber() == 308) {
                return "Mauvais entretien des équipements";
            } else if (this.getViolNumber() == 309) {
                return "produits chimiques de nettoyage mal stockés";
            } else if (this.getViolNumber() == 310) {
                return "articles à usage unique non éliminés après utilisation";
            } else if (this.getViolNumber() == 311) {
                return "Locaux mal entretenus";
            } else if (this.getViolNumber() == 312) {
                return "articles hors cuisine stockés sur place";
            } else if (this.getViolNumber() == 313) {
                return "animal non-aidant sur place";
            } else if (this.getViolNumber() == 314) {
                return "Mauvaises procédures sanitaires écrites";
            } else if (this.getViolNumber() == 315) {
                return "thermomètres inexacts ou inexistants";
            } else if (this.getViolNumber() == 401) {
                return "stations de lavage des mains médiocres ou inexistantes";
            } else if (this.getViolNumber() == 402) {
                return "Mauvaise pratique de lavage des mains des employés";
            } else if (this.getViolNumber() == 403) {
                return "mauvaise hygiène personnelle des employés";
            } else if (this.getViolNumber() == 404) {
                return "Employé qui fume près des zones critiques";
            } else if (this.getViolNumber() == 501) {
                return "l'opérateur n'a pas la certification FOODSAFE";
            } else if (this.getViolNumber() == 502) {
                return "pas de certification FOODSAFE sur site";
            } else {
                return "Violation générique trouvée";
            }
        } else if(Locale.getDefault().getLanguage()=="es") {
            if (this.getViolNumber () == 101) {
                return "Planes de construcción ignorando regulaciones";
            } else if (this.getViolNumber () == 102) {
                return "premisa no aprobada";
            } else if (this.getViolNumber () == 103) {
                return "Permiso inválido";
            } else if (this.getViolNumber () == 104) {
                return "permiso oculto";
            } else if (this.getViolNumber () == 201) {
                return "alimentos contaminados";
            } else if (this.getViolNumber () == 202) {
                return "Alimentos mal procesados";
            } else if (this.getViolNumber () == 203) {
                return "la comida no se enfría adecuadamente";
            } else if (this.getViolNumber () == 204) {
                return "La comida no se cocina correctamente";
            } else if (this.getViolNumber () == 205) {
                return "alimentos fríos mal almacenados";
            } else if (this.getViolNumber () == 206) {
                return "comida caliente mal almacenada";
            } else if (this.getViolNumber () == 208) {
                return "Alimentos obtenidos de una fuente no aprobada";
            } else if (this.getViolNumber () == 209) {
                return "Alimentos no protegidos contra la contaminación";
            } else if (this.getViolNumber () == 210) {
                return "alimentos no descongelados correctamente";
            } else if (this.getViolNumber () == 211) {
                return "los alimentos congelados no se almacenan correctamente";
            } else if (this.getViolNumber () == 212) {
                return "Procedimientos de manejo de alimentos por escrito deficientes";
            } else if (this.getViolNumber () == 301) {
                return "Superficies y equipos de cocina sucios";
            } else if (this.getViolNumber () == 302) {
                return "Superficies y equipos de cocina sucios";
            } else if (this.getViolNumber () == 303) {
                return "Mal equipo o instalaciones sanitarias";
            } else if (this.getViolNumber () == 304) {
                return "plagas encontradas localmente";
            } else if (this.getViolNumber () == 305) {
                return "prevención de malas plagas";
            } else if (this.getViolNumber () == 306) {
                return "locales insalubres";
            } else if (this.getViolNumber () == 307) {
                return "Materiales o utensilios de mala calidad";
            } else if (this.getViolNumber () == 308) {
                return "Mantenimiento deficiente del equipo";
            } else if (this.getViolNumber () == 309) {
                return "productos químicos de limpieza almacenados incorrectamente";
            } else if (this.getViolNumber () == 310) {
                return "artículos de un solo uso no desechados después del uso";
            } else if (this.getViolNumber () == 311) {
                return "Locales mal mantenidos";
            } else if (this.getViolNumber () == 312) {
                return "artículos que no sean de cocina almacenados en el sitio";
            } else if (this.getViolNumber () == 313) {
                return "animal que no ayuda en el sitio";
            } else if (this.getViolNumber () == 314) {
                return "Procedimientos sanitarios escritos incorrectos";
            } else if (this.getViolNumber () == 315) {
                return "termómetros inexactos o inexistentes";
            } else if (this.getViolNumber () == 401) {
                return "estaciones de lavado de manos pobres o inexistentes";
            } else if (this.getViolNumber () == 402) {
                return "Mala práctica de lavar las manos de los empleados";
            } else if (this.getViolNumber () == 403) {
                return "mala higiene personal de los empleados";
            } else if (this.getViolNumber () == 404) {
                return "Empleado que fuma cerca de áreas críticas";
            } else if (this.getViolNumber () == 501) {
                return "el operador no tiene certificación FOODSAFE";
            } else if (this.getViolNumber () == 502) {
                return "sin certificación FOODSAFE en el sitio";
            } else {
                return "Violación genérica encontrada";
            }
        } else {
            return briefDetails;
        }
    }

    public Boolean getRepeat() {
        return repeat;
    }
}
