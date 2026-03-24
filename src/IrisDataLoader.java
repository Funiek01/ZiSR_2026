import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IrisDataLoader {

    public static List<IrisSample> load(String filePath) throws IOException {
        List<IrisSample> samples = new ArrayList<IrisSample>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length != 5) {
                    continue;
                }

                double[] features = new double[4];
                for (int i = 0; i < 4; i++) {
                    features[i] = Double.parseDouble(parts[i]);
                }

                samples.add(new IrisSample(features, parts[4]));
            }
        } finally {
            reader.close();
        }

        return samples;
    }
}
