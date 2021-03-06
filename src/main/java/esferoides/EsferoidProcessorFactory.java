package esferoides;

/**
 * 
 * @author Jonathan
 * @see <a href = "https://github.com/joheras/SpheroidJ" > Github repository
 *      </a>
 *
 *
 */
public class EsferoidProcessorFactory {

	public static EsferoidProcessor createEsferoidProcessor(String name) {

		EsferoidProcessor esferoidProcessor = null;

		switch (name) {
		case "Fluorescence": {

			esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesFluo,
					DetectEsferoidMethods::detectEsferoideFluoSuspension);

			break;
		}
		case "colageno": {

			esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesFluo,
					DetectEsferoidMethods::detectEsferoideFluoColageno);

			break;
		}
		case "Edges": {

			esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesHectorNoFluo,
					DetectEsferoidMethods::detectEsferoideHectorv1);

			break;
		}
		case "Hector no fluo v2": {

			esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesHectorNoFluo,
					DetectEsferoidMethods::detectEsferoideHectorv2);

			break;
		}
		case "Threshold + Edges": {

			esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesTeodora,
					DetectEsferoidMethods::detectEsferoideTeodora);

			break;
		}
		case "Threshold & Edges": {

			esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesTeodora,
					DetectEsferoidMethods::detectEsferoideTeodoraBig);

			break;
		}
		case "Teodora No Holes": {
			esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesJPG, // searchFilesTeodora,
					DetectEsferoidMethods::detectEsferoideTeodoraBigNoHoles);
			break;
		}
		case "Hector fluo stack": {

			esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesTeodora,
					DetectEsferoidMethods::detectEsferoideFluoStack);

			break;
		}

		case "Threshold": {

			esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFilesJPG,
					DetectEsferoidMethods::detectEsferoideTeniposide);

			break;
		}
		case "HRNSeg": {
			esferoidProcessor = new EsferoidProcessor(SearchFilesMethods::searchFiles,
					DetectEsferoidMethods::detectEsferoideDeep);
			break;
		}
		}

		return esferoidProcessor;

	}

}
