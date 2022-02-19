
import lib.Loss;

public class Model {

	private			double		accuracy = 0;
	private			DataSet 	trainData;
	private			DataSet 	validateData;
	private final	Layer[]		LAYERS;

	private Model(final Layer[] L){
		LAYERS = L;
	}

	public static Model Sequential(final Layer ... L){
		return new Model(L);
	}


	


	private boolean feedForward(final int SAMPLE_INDEX){

		return true;
	}

	private void backPropagate(){}

	private void weightsUpdate(){}




	/**
	 * 
	 * @param L
	 */
	public void buildStructure(final DataSet DATA, final Loss L){
		if(this.LAYERS.length < 1) throw new ExceptionInInitializerError();

		this.LAYERS[0].layerInit(DATA.getSample(0));	// initialising the input layer
		Layer prevLayer = this.LAYERS[0];				// previous layer

		for(int i=1; i < this.LAYERS.length; i++){
			this.LAYERS[i].layerInit(prevLayer.getNodes());
			prevLayer = this.LAYERS[i];
		}
	}

	/**
	 * Model training
	 * @param DATA dataset
	 * @param BATCH mini batch size
	 * @param EPOCHS cicles of entire dataset
	 */
	public void train(final DataSet DATA,final int BATCH, final int EPOCHS){
		this.trainData = DATA;
		int miniBatch = 0;
		final int DATA_SIZE = this.trainData.getSize();
		
		//cicling over the dataset samples for "EPOCHS" times
		for(int e=0; e < EPOCHS; e++){
			this.trainData.shuffle();	// shuffeling the samples

			for(int sample=0; sample < DATA_SIZE; sample++){
				feedForward(sample++);
				backPropagate();

				// updating weights after a certain amount of samples (mini batch)
				if(++miniBatch == BATCH || sample == DATA_SIZE-1){
					weightsUpdate();
					miniBatch = 0;
				}
			}
		}
	}


	/**
	 * Validating / testing the model
	 * @param DATA dataset
	 */
    public double validate(final DataSet DATA){
		this.validateData = DATA;
		int correct = 0;

		for(int sample=0; sample < this.validateData.getSize(); sample++){
			if(feedForward(sample)) correct++;
		}

		// storing the accuracy percentage
		this.accuracy = correct * 100 / validateData.getSize();
		return this.accuracy;
	}


	// getting the model accuracy
	public double getAccuracy(){
		return this.accuracy;
	}


}
