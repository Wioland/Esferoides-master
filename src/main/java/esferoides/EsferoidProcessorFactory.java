package esferoides;

public class EsferoidProcessorFactory {

	public static EsferoidProcessor createEsferoidProcessor(String name, boolean all) {

		EsferoidProcessor esferoidProcessor = null;

		switch (name) {
		case "suspension": {
			if (all) {
				esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesFluo,
						DetectEsferoidMethods::detectEsferoideFluoSuspensionALLPRocess);
			} else {
				esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesFluo,
						DetectEsferoidMethods::detectEsferoideFluoSuspension);
			}

			break;
		}
		case "colageno": {
			if (all) {
				esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesFluo,
						DetectEsferoidMethods::detectEsferoideFluoColagenoALLPRocess);
			} else {
				esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesFluo,
						DetectEsferoidMethods::detectEsferoideFluoColageno);
			}

			break;
		}
		case "Hector no fluo v1": {
			if (all) {
				esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesHectorNoFluo,
						DetectEsferoidMethods::detectEsferoideHectorv1ALLPRocess);
			} else {
				esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesHectorNoFluo,
						DetectEsferoidMethods::detectEsferoideHectorv1);
			}

			break;
		}
		case "Hector no fluo v2": {
			if (all) {
				esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesHectorNoFluo,
						DetectEsferoidMethods::detectEsferoideHectorv2ALLPRocess);
			} else {
				esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesHectorNoFluo,
						DetectEsferoidMethods::detectEsferoideHectorv2);
			}

			break;
		}
		case "Teodora v1": {
			if (all) {
				esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesTeodora,
						DetectEsferoidMethods::detectEsferoideTeodoraALLPRocess);
			} else {
				esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesTeodora,
						DetectEsferoidMethods::detectEsferoideTeodora);
			}

			break;
		}
		case "Teodora Big": {
			if (all) {
				esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesTeodora,
						DetectEsferoidMethods::detectEsferoideTeodoraBigALLPRocess);
			} else {
				esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesTeodora,
						DetectEsferoidMethods::detectEsferoideTeodoraBig);
			}

			break;
		}

		}

		return esferoidProcessor;

	}

}
