package angeelya.inPic.recommedation.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SlopeOne {
    private  Map<Long, Map<Long, Double>> mData;
    private  Map<Long, Map<Long, Double>> diffMatrix;
    private  Map<Long, Map<Long, Integer>> freqMatrix;
    private  HashMap<Long, Double> predictions;
    private  HashMap<Long, Integer> frequencies;

    public  Map<Long, Double> beginSlopeOne(Map<Long, Map<Long, Double>> data, Long user_id) {
        mData = data;
        buildAverageDiffMatrix();
        return predict(data.get(user_id));
    }

    private  Map<Long, Double> predict(Map<Long, Double> user) {
        createPredictionsAndFrequencies();
        fillPredictionsAndFrequencies(user);
        HashMap<Long, Double> cleanPredictions = new HashMap<>();
        for (Long j : predictions.keySet()) {
            if (frequencies.get(j) > 0) {
                cleanPredictions.put(j, predictions.get(j) / frequencies.get(j).intValue());
            }
        }
        for (Long j : user.keySet()) {
            cleanPredictions.put(j, user.get(j));
        }
        return cleanPredictions;
    }

    private  void createPredictionsAndFrequencies() {
        predictions = new HashMap<>();
        frequencies = new HashMap<>();
        for (Long i : diffMatrix.keySet()) {
            frequencies.put(i, 0);
            predictions.put(i, 0.0);
        }
    }

    private  void fillPredictionsAndFrequencies(Map<Long, Double> user) {
        for (Long j : user.keySet()) {
            for (Long k : diffMatrix.keySet()) {
                try {
                    Double newVal = (diffMatrix.get(k).get(j) + user.get(j)) * freqMatrix.get(k).get(j).intValue();
                    predictions.put(k, predictions.get(k) + newVal);
                    frequencies.put(k, frequencies.get(k) + freqMatrix.get(k).get(j).intValue());
                } catch (NullPointerException e) {
                }
            }
        }
    }

    private  void buildAverageDiffMatrix() {
        diffMatrix = new HashMap<>();
        freqMatrix = new HashMap<>();
        // first iterate through users
        for (Map<Long, Double> user : mData.values()) {
            // then iterate through user data
            for (Map.Entry<Long, Double> entry : user.entrySet()) {
                Long i1 = entry.getKey();
                double g1 = entry.getValue();

                if (!diffMatrix.containsKey(i1)) {
                    diffMatrix.put(i1, new HashMap<>());
                    freqMatrix.put(i1, new HashMap<>());
                }

                for (Map.Entry<Long, Double> entry2 : user.entrySet()) {
                    Long i2 = entry2.getKey();
                    double g2 = entry2.getValue();

                    int cnt = 0;
                    if (freqMatrix.get(i1).containsKey(i2))
                        cnt = freqMatrix.get(i1).get(i2);
                    double diff = 0.0;
                    if (diffMatrix.get(i1).containsKey(i2))
                        diff = diffMatrix.get(i1).get(i2);
                    double new_diff = g1 - g2;
                    freqMatrix.get(i1).put(i2, cnt + 1);
                    diffMatrix.get(i1).put(i2, diff + new_diff);
                }
            }
        }
        getAverageDiffMatrix(diffMatrix, freqMatrix);
    }

    private static void getAverageDiffMatrix(Map<Long, Map<Long, Double>> diffMatrix, Map<Long, Map<Long, Integer>> freqMatrix) {
        for (Long j : diffMatrix.keySet()) {
            for (Long i : diffMatrix.get(j).keySet()) {
                Double oldValue = diffMatrix.get(j).get(i);
                int count = freqMatrix.get(j).get(i).intValue();
                diffMatrix.get(j).put(i, oldValue / count);
            }
        }
    }

}
