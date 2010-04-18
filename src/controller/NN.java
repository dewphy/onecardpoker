package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

/**
 * Back-Propagation Neural Networks
 */
public class NN {
	
	private double N;
	private double M;
  
	private final int ni;
	private final int nh;
	private final int no;
	
	private double[] ai;
	private double[] ah;
	private double[] ao;
	
	private double[][] wi;
	private double[][] wo;
	
	private double[][] ci;
	private double[][] co;
	
	private Random random = new Random(0);
	
	/**
	 * Creates a Neural Network with one hidden layer.
	 * @param ni the number of input neurons.
	 * @param nh the number of hidden neurons.
	 * @param no the number of output neurons.
	 * @param N the learning rate.
	 * @param M the momentum factor.
	 */
	public NN(int ni, int nh, int no) {
		
		// Initialize neural network structure.
		this.ni = ni + 1; // +1 for bias node.
		this.nh = nh;
		this.no = no;
		
		// Initialize activations neurons.
		ai = createVector(this.ni, 1.0);
		ah = createVector(this.nh, 1.0);
		ao = createVector(this.no, 1.0);
    
		// Initialize weights
		wi = createMatrix(this.ni, this.nh, -1.0, 1.0);
		wo = createMatrix(this.nh, this.no, -1.0, 1.0);
		
		// Initialize last changes in weights for momentum.
		this.ci = createMatrix(this.ni, this.nh, 0.0);
		this.co = createMatrix(this.nh, this.no, 0.0);
	}
	
	public double[] update(double[] inputs) {
		if (inputs.length != ni-1) {
			System.out.println("Wrong number of inputs. Received: " + inputs.length + ". Expected: " + (ni-1));
			// throw new Exception("Wrong number of inputs. Received: " + inputs.length + ". Expected: " + (ni-1));
		}
		
		// Activates inputs neurons.
		for (int i=0; i<ni-1; i++) {
			// ai[i] = sigmoid(inputs[i]);
			ai[i] = inputs[i];
		}
		
		// Activates hidden neurons.
		for (int j=0; j<nh; j++) {
			double sum = 0.0;
			for (int i=0; i<ni; i++) {
				sum += ai[i] * wi[i][j];
			}
			ah[j] = sigmoid(sum);
		}
		
		// Activates output neurons.
		for (int k=0; k<no; k++) {
			double sum = 0.0;
			for (int j=0; j<nh; j++) {
				sum += ah[j] * wo[j][k];
			}
			ao[k] = sigmoid(sum);
		}
		
		// Copies activated output.
		double[] targets = new double[no];
		for (int i=0; i<no; i++) {
			targets[i] = ao[i];
		}
		
		return targets;
	}
	
	public void test(double[][] inputs, double[][] targets) {
		for (int i=0; i<inputs.length; i++) {
			double[] outputs = update(inputs[i]);
			this.printVector(inputs[i], " Inputs");
			this.printVector(targets[i], "Targets");
			this.printVector(outputs, "Outputs");
		}
	}
	 
	public double backPropagate(double[] targets) {
		if (targets.length != no) {
			// throw new Exception("Wrong number of targets. Received: " + targets.length + ". Expected: " + no);
		}
		
		// Computes error for output neurons.
		double[] oDelta = createVector(no,0.0);
		for (int k=0; k<no; k++) {
			double error = targets[k]-ao[k];
			oDelta[k] = dsigmoid(ao[k]) * error;
		}
		
		// Computes error for hidden neurons.
		double[] hDelta = createVector(nh,0.0);
		for (int j=0; j<nh; j++) {
			double error = 0.0;
			for (int k=0; k<no; k++) {
				error += oDelta[k] * wo[j][k];
			}
			hDelta[j] = dsigmoid(ah[j]) * error;
		}
		
		// Update output weights.
		for (int j=0; j<nh; j++) {
			for (int k=0; k<no; k++) {
				double change = oDelta[k] * ah[j];
				wo[j][k] += N*change + M*co[j][k];
				co[j][k] = change;
			}
		}
		
		// Update input weights.
		for (int i=0; i<ni; i++) {
			for (int j=0; j<nh; j++) {
				double change = hDelta[j] * ai[i];
				wi[i][j] += N*change + M*ci[i][j];
				ci[i][j] = change;
			}
		}
		
		// Calculate error.
		double error = 0.0;
		for (int k=0; k<no; k++) {
			error += 0.5*(targets[k]-ao[k])*(targets[k]-ao[k]);
		}
		return error;
	}
	
	/**
	 * Returns a vector with the given length containing the given value.
	 * @param length
	 * @param value
	 */
	private double[] createVector(int length, double value) {
		double[] vector = new double[length];
		for (int i=0; i<length; i++) {
			vector[i] = value;
		}
		return vector;
	}
	
	/**
	 * Returns a matrix with the given length containing random values between a and b.
	 * @param lenght
	 * @param height
	 * @return
	 */
	private double[][] createMatrix(int height, int lenght, double a, double b) {
		double[][] matrix = new double[height][lenght];
		for (int i=0; i<height; i++) {
			for (int j=0; j<lenght; j++) {
				matrix[i][j] = a + (b-a)*random.nextDouble();
			}
		}
		return matrix;
	}
	
	/**
	 * Returns a matrix with the given length containing random values between a and b.
	 * @param lenght
	 * @param height
	 * @return
	 */
	private double[][] createMatrix(int height, int lenght, double value) {
		return createMatrix(height, lenght, value, value);
	}
	
	public void printWeights() {
		printMatrix(wi, "Input weights");
		printMatrix(wo, "Output weights");
	}
	
	private void printMatrix(double[][] matrix, String name) {
		System.out.println(name + ":");
		for (int i=0; i<matrix.length; i++) {
			for (int j=0; j<matrix[i].length; j++) {
				System.out.print(" " + matrix[i][j]);
			}
			System.out.println();
		}
	}
	
	private void printVector(double[] vector, String name) {
		System.out.print(name + ":  ");
		for (int i=0; i<vector.length; i++) {
			System.out.print(" " + vector[i]);
		}
		System.out.println();
	}
	
	public void train(double[][] inputs, double[][] targets, int nbIterations, double N, double M) {
		// Initialize learning parameters.
		this.N = N;
		this.M = M;
		
		for (int iter=0; iter<nbIterations; iter++) {
			double error = 0.0;
			for (int i=0; i<inputs.length; i++) {
				update(inputs[i]);
				error += backPropagate(targets[i]);
			}
			if (iter%1 == 0) {
				// System.out.println("iteration: " + iter + ", error: " + error);
			}
		}
	}
	
	/**
	 * Returns the value of the sigmoid function for the given abscissa.
	 * @param x the abscissa
	 * @return the value of the sigmoid function for the given abscissa.
	 */
	private double sigmoid(double x) {
		return Math.tanh(x);
	}
	
	/**
	 * Returns the value of the derivative of the sigmoid function for the given abscissa.
	 * @param x the abscissa
	 * @return the value of the derivative of the sigmoid function for the given abscissa
	 */
	private double dsigmoid(double x) {
    return 1.0-x*x;
	}
}
