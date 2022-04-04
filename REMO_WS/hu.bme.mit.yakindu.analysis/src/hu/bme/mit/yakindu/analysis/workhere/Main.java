package hu.bme.mit.yakindu.analysis.workhere;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
				Statechart s = (Statechart) root;
				TreeIterator<EObject> iterator = s.eAllContents();
				while (iterator.hasNext()) {
					EObject content = iterator.next();
					if(content instanceof State) {
						State state = (State) content;
						System.out.println(state.getName());
					}
					if(content instanceof Transition) {
						Transition transition =(Transition) content;
						System.out.println(transition.getSource().getName() + "->" + transition.getTarget().getName());
					}
				}
				
				List<State> leafStates = findLeaf(s.eAllContents());
				System.out.println("LeafStates: ");
				for (int i = 0; i < leafStates.size(); i++) {
					System.out.println(leafStates.get(i).getName());
				}
				
				List<State> noNameStates = findStatesWithoutName(s.eAllContents());
				System.out.println("noNameStates: ");
				for (int i = 0; i < noNameStates.size(); i++) {
					giveName(s);
				}
				
				// Transforming the model into a graph representation
				String content = model2gml.transform(root);
				// and saving it
				manager.saveFile("model_output/graph.gml", content);
	}
	
	private static List<State> findLeaf(TreeIterator<EObject> iterator) {
		List<State> leafStates = new ArrayList<>();
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof State) {
				State state = (State) content;
				if (state.getOutgoingTransitions().size() == 0) {
					leafStates.add(state);
				}
			}
		}
		return leafStates;
	}
	
	private static List<State> findStatesWithoutName(TreeIterator<EObject> iterator) {
		List<State> noNameStates = new ArrayList<>();
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof State) {
				State state = (State) content;
				if (state.getName() == null) {
					noNameStates.add(state);
				}
			}
		}
		return noNameStates;
	}
	
	private static void giveName(Statechart s) {
		int cnt = 0;
		String name = null;
		TreeIterator<EObject> iterator = s.eAllContents();
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof State) {
				State state = (State) content;
				if (state.getName() == "State-" + Integer.toString(cnt)) {
					cnt++;
					iterator = s.eAllContents();
				}
				//System.out.println(state.getName());
			}
		}
 		System.out.println("State-" + Integer.toString(cnt));
	}
}
