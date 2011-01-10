package similarity;

import java.util.Map;

public class Similarity {

	public static float calcCosineSimilarity(Map<String, Float> ftrVector1,
			Map<String, Float> ftrVector2) {

		float numerator = 0, vctr1Norm = 0, vctr2Norm = 0;
		for (Map.Entry<String, Float> ftr1 : ftrVector1.entrySet()) {
			Float ftr2Val = ftrVector2.get(ftr1.getKey());
			if (ftr2Val != null) {
				numerator += ftr1.getValue() * ftr2Val;
			}
			vctr1Norm += ftr1.getValue() * ftr1.getValue();
		}
		for (Float ftr2 : ftrVector2.values()) {
			vctr2Norm += ftr2 * ftr2;
		}

		return (float) (numerator / (Math.sqrt(vctr1Norm) * Math
				.sqrt(vctr2Norm)));
	}

	public static float calcCorrelation(Map<String, Float> ftrVector1,
			Map<String, Float> ftrVector2) {
		// TODO check the implementation, n = max vector length ?

		int n = Math.max(ftrVector1.size(), ftrVector2.size());
		float segmaXiYi = 0, segmaXi = 0, segmaYi = 0, segmaXiSqr = 0, segmaYiSqr = 0;

		for (Map.Entry<String, Float> ftr1 : ftrVector1.entrySet()) {
			Float ftr2Val = ftrVector2.get(ftr1.getKey());
			if (ftr2Val != null) {
				segmaXiYi += ftr1.getValue() * ftr2Val;
			}
			segmaXi += ftr1.getValue();
			segmaXiSqr += ftr1.getValue() * ftr1.getValue();
		}
		for (Float ftr2 : ftrVector2.values()) {
			segmaYiSqr += ftr2 * ftr2;
			segmaYi += ftr2;
		}
		
		float numerator = n * segmaXiYi - segmaXi * segmaYi;
		float xStdDev = n * segmaXiSqr - segmaXi * segmaXi;
		float yStdDev = n * segmaYiSqr - segmaYi * segmaYi;
		

		return (float) (numerator / (Math.sqrt(xStdDev) * Math.sqrt(yStdDev)));
	}
}
