package bayern.mimo.masterarbeit.common;


import java.util.LinkedList;
import java.util.List;

import weka.knowledgeflow.steps.Classifier;

/**
 * Created by MiMo
 */

public class ClassificationHelper {

    private static Classifier classifier;

    private ClassificationHelper() {}

    public static String classify(){
        return classifyMultiple().get(0);
    }

    public static List<String> classifyMultiple(){
        List<String> mostProbableCategories = new LinkedList<>();
        return mostProbableCategories;
    }




    private static boolean loadModel(){


        

        // return true if model has been loaded



        return true;
    }



}
