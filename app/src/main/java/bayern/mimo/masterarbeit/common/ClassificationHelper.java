package bayern.mimo.masterarbeit.common;


import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bayern.mimo.masterarbeit.data.DataRecording;
import bayern.mimo.masterarbeit.data.ShimmerValue;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ConverterUtils.DataSource;


/**
 * Created by MiMo
 */

public class ClassificationHelper {

    private static Classifier classifier;

    private ClassificationHelper() {
    }

    public static String classify(DataRecording record) {

        FastVector rawVector = new FastVector(5);
        rawVector.addElement(new Attribute("accellnz"));
        rawVector.addElement(new Attribute("accelwrz"));
        rawVector.addElement(new Attribute("timestamp"));

        /*
        String[] positionLabels = {"front", "rear"};
        FastVector positionVals = new FastVector(positionLabels.length - 1);
        for (int i = 0; i < positionLabels.length; i++) {
            positionVals.addElement(positionLabels[i]);
        }
        rawVector.addElement(new Attribute("position", positionVals));
*/


        String[] classLabels = {"null", "S0", "S1", "S2", "S3", "S4", "S5"};
        FastVector classVals = new FastVector(classLabels.length - 1);
        for (int i = 1; i < classLabels.length; i++) { //TODO hier evtl. auf 1 erhöhen / auf 0 erniedrigen, damit NULL nicht dabei ist
            classVals.addElement(classLabels);
        }
        /*rawVector.addElement(new Attribute("class", classVals));


        Instances rawDataset = new Instances("RawDataset", rawVector, 0);
        rawDataset.setClassIndex(rawDataset.numAttributes() - 1);
*/

        FastVector classifiedVector = new FastVector(3);
        classifiedVector.addElement(new Attribute("accellnz"));
        classifiedVector.addElement(new Attribute("accelwrz"));

        FastVector classifiedClassVals = new FastVector(classLabels.length - 1);
        for (int i = 1; i < classLabels.length; i++) {
            classifiedClassVals.addElement(classLabels[i]);
        }
        classifiedVector.addElement(new Attribute("class", classifiedClassVals));


        Instances classifiedDataset = new Instances("Dataset", classifiedVector, 0);
        classifiedDataset.setClassIndex(classifiedDataset.numAttributes() - 1);


        fillDatasetSlidingWindowLike(classifiedDataset, record);


        Map<String, Integer> results = classifyMultiple(classifiedDataset);

        String mostProbableCategories = getStringOfClassList(results);

        if (mostProbableCategories.isEmpty())
            return "leer";

        return mostProbableCategories;
    }

    private static String getStringOfClassList(Map<String, Integer> classes) {
        String result = "";
        for(String className : classes.keySet()){
            Integer occurences = classes.get(className);
            if(!result.isEmpty()) result += ", ";
            result += className + ": " + occurences;
        }

        return result;

    }

    private static void fillDatasetSlidingWindowLike(Instances dataset, DataRecording record) {

        int malnehmen = 1000;

        int windowSize = 40000;
        int jump = 25000;

        Object[] shimmerMACs = record.getShimmerValues().keySet().toArray();
        List<ShimmerValue> s1Vals = new LinkedList<ShimmerValue>();
        try {
            s1Vals = record.getShimmerValues().get(shimmerMACs[0].toString());
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        //System.out.println("s1Vals: " + s1Vals.size());

        List<ShimmerValue> s2Vals = new LinkedList<ShimmerValue>();
        try {
            s2Vals = record.getShimmerValues().get(shimmerMACs[1].toString());
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        //System.out.println("s2Vals: " + s2Vals.size());

        List<ShimmerValue> s1WindowVals = new LinkedList<>();
        List<ShimmerValue> s2WindowVals = new LinkedList<>();

        long maxTS1 = 0;
        long minTS1 = Long.MAX_VALUE;

        long maxTS2 = 0;
        long minTS2 = Long.MAX_VALUE;

        for (ShimmerValue value : s1Vals) {

            if (maxTS1 < value.getTimestamp() * malnehmen)
                maxTS1 = (long) (value.getTimestamp() * malnehmen);
            if (minTS1 > value.getTimestamp() * malnehmen)
                minTS1 = (long) (value.getTimestamp() * malnehmen);
        }
        for (ShimmerValue value : s2Vals) {

            if (maxTS2 < value.getTimestamp() * malnehmen)
                maxTS2 = (long) (value.getTimestamp() * malnehmen);
            if (minTS2 > value.getTimestamp() * malnehmen)
                minTS2 = (long) (value.getTimestamp() * malnehmen);
        }

        /*
        System.out.println("maxTS1: " + maxTS1);
        System.out.println("minTS1: " + minTS1);

        System.out.println("maxTS2: " + maxTS2);
        System.out.println("minTS2: " + minTS2);
*/


        long currentStartTS = 0;
        long currentEndTS = windowSize;
        boolean goOn = true;

        Instance instance;

        int instanceCounter = 0;

        /*
        if (s1Vals.size() > 0)
            System.out.println("drecks Timestamp is " + s1Vals.get(0).getTimestamp());
*/

        while (goOn) {
            s1WindowVals.clear();

            for (ShimmerValue value : s1Vals) {
                long ts = (long) (value.getTimestamp() * malnehmen);
                if (ts >= currentStartTS && ts <= currentEndTS)
                    s1WindowVals.add(value);
            }


            double[] mean = calcMean(s1WindowVals);

            instance = new SparseInstance(3);
            instance.setValue(dataset.attribute(0), mean[0]);
            instance.setValue(dataset.attribute(1), mean[1]);
            instance.setValue(dataset.attribute(2), "S0");

            dataset.add(instance);

            instanceCounter++;
            if (instanceCounter % 1000 == 0) {
                System.out.println(instanceCounter + " Instanzen erstellt");
            }

            //System.out.println("added instance (1)");

            if (currentStartTS > maxTS1)
                goOn = false;

            currentStartTS += jump;
            currentEndTS += jump;
        }


        currentStartTS = 0;
        currentEndTS = windowSize;

        instanceCounter = 0;
        goOn = true;
        while (goOn) {
            s2WindowVals.clear();

            for (ShimmerValue value : s2Vals) {
                long ts = (long) (value.getTimestamp() * malnehmen);
                if (ts >= currentStartTS && ts <= currentEndTS)
                    s2WindowVals.add(value);
            }

            double[] mean = calcMean(s2WindowVals);

            instance = new SparseInstance(3);
            instance.setValue(dataset.attribute(0), mean[0]);
            instance.setValue(dataset.attribute(1), mean[1]);
            instance.setValue(dataset.attribute(2), "S0");

            dataset.add(instance);

            instanceCounter++;

            if (instanceCounter % 1000 == 0) {
                System.out.println(instanceCounter + " Instanzen erstellt");
            }

            //System.out.println("added instance (2)");

            if (currentStartTS > maxTS2)
                goOn = false;

            currentStartTS += jump;
            currentEndTS += jump;
        }

        System.out.println("FERTIG MIT DINGENS");

    }


    private static double[] calcMean(List<ShimmerValue> values) {

        double[] result = new double[2];

        double sumAccelLnZ = 0;
        double sumAccelWrZ = 0;

        for (ShimmerValue value : values) {
            sumAccelLnZ += value.getAccelLnZ();
            sumAccelWrZ += value.getAccelWrZ();
        }

        result[0] = sumAccelLnZ / (double) values.size();
        result[1] = sumAccelWrZ / (double) values.size();

        return result;
    }

    public static Map<String, Integer> classifyMultiple(Instances dataset) {

        loadModel();

        List<String> allCategories = new LinkedList<>();

        int[] frequencies = new int[6];
        for (int i = 0; i < frequencies.length; i++)
            frequencies[i] = 0;

        for (int i = 0; i < dataset.numInstances(); i++) {
            try {
                double clsLabel = classifier.classifyInstance(dataset.instance(i));
                //System.out.println("class label is " + dataset.classAttribute().value((int) clsLabel));
                allCategories.add(dataset.classAttribute().value((int) clsLabel));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Map<String, Integer> results = new HashMap<>();

        int s0Count = 0;
        int s1Count = 0;
        int s2Count = 0;
        int s3Count = 0;
        int s4Count = 0;
        int s5Count = 0;

        for (String category : allCategories) {
            switch (category) {
                case "S0":
                    s0Count++;
                    break;
                case "S1":
                    s1Count++;
                    break;
                case "S2":
                    s2Count++;
                    break;
                case "S3":
                    s3Count++;
                    break;
                case "S4":
                    s4Count++;
                    break;
                case "S5":
                    s5Count++;
                    break;
            }
        }


        results.put("S0", s0Count);
        results.put("S1", s1Count);
        results.put("S2", s2Count);
        results.put("S3", s3Count);
        results.put("S4", s4Count);
        results.put("S5", s5Count);

        return sortByValue(results);
    }


    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }


    private static boolean loadModel() {
/*
        Uri arff = Uri.parse("file:///android_asset/ds.arff");
        File arffFile = new File(arff.getPath());
        String filePath = arffFile.getAbsolutePath();
*/

        classifier = null;
        try {

            // deserialize model
            classifier = (Classifier) weka.core.SerializationHelper.read(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ma/arff/j48.model");
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("classifier is " + classifier);

        if (classifier == null) {


            try {


                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ma/arff/ds.arff";

                File testfile = new File(filePath);
                if (testfile.exists()) System.out.println("testfile exists!");
                else System.out.println("testfile does not exist!");


                System.out.println("file path of asset ds.arff file is '" + filePath + "'");

                DataSource source = new DataSource(filePath);

                if (source == null) System.out.println("source is null!");
                System.out.println("source is " + source.toString());


                Instances data = source.getDataSet();
                if (data.classIndex() == -1)
                    data.setClassIndex(data.numAttributes() - 1);

                System.out.println("dataset loaded");


                //TODO standard-options, vllt. ändern
                String[] options = new String[4];
                options[0] = "-C";
                options[1] = "0.25";
                options[2] = "-M";
                options[3] = "2";
                classifier = new J48();
                classifier.setOptions(options);
                classifier.buildClassifier(data);

                System.out.println("classifier built");

                // serialize model
                weka.core.SerializationHelper.write(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ma/arff/j48.model", classifier);

            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("classifier after is " + classifier);

        }
        // return true if model has been loaded

        //TODO hier sollten wir einen classifier haben - globale Variable oder so

        return true;
    }


}
