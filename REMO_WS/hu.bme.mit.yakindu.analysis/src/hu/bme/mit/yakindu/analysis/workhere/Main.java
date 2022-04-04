package hu.bme.mit.yakindu.analysis.workhere;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.base.types.Event;
import org.yakindu.base.types.Property;
import org.yakindu.sct.model.sgraph.Scope;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

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
				
				System.out.println(serialization(traversal(s)));
				
				// Transforming the model into a graph representation
				String content = model2gml.transform(root);
				// and saving it
				manager.saveFile("model_output/graph.gml", content);
				manager.saveFile("src/hu/bme/mit/yakindu/analysis/workhere/main2.java", serialization(traversal(s)));
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
	
	private static List<List<String>> traversal(Statechart s) {
		EList<Scope> scopes = s.getScopes();
		int i = 0;
		List<List<String>> scInterface = new ArrayList<>();
		List<String> eL = new ArrayList<>();
		List<String> vL = new ArrayList<>();
		while (i < scopes.size()) {
			EObject content = scopes.get(i);
			EList<Event> events = scopes.get(i).getEvents();
			EList<Property> variables = scopes.get(i).getVariables();
			
			for (int j = 0; j < events.size(); j++ ) {
				eL.add(events.get(j).getName());
				System.out.println(events.get(j).getName());
			}
			
			for (int j = 0; j < variables.size(); j++ ) {
				vL.add(variables.get(j).getName());
				System.out.println(variables.get(j).getName());
			}
			
			i++;
		}
		scInterface.add(vL);
		scInterface.add(eL);
		
		return scInterface;
	}
	
	private static String serialization(List<List<String>> scInterface) {
		String mainClass = "package hu.bme.mit.yakindu.analysis.workhere;\r\n" + 
				"\r\n" + 
				"import java.io.IOException;\r\n" + 
				"import java.util.Scanner;\r\n" + 
				"\r\n" + 
				"import hu.bme.mit.yakindu.analysis.RuntimeService;\r\n" + 
				"import hu.bme.mit.yakindu.analysis.TimerService;\r\n" + 
				"import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;\r\n" + 
				"import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"public class main2 {\r\n" + 
				"	\r\n" + 
				"	public static void main(String[] args) throws IOException {\r\n" + 
				"		ExampleStatemachine s = new ExampleStatemachine();\r\n" + 
				"		s.setTimer(new TimerService());\r\n" + 
				"		RuntimeService.getInstance().registerStatemachine(s, 200);\r\n" + 
				"		s.init();\r\n" + 
				"		s.enter();\r\n" + 
				"		s.runCycle();\r\n" + 
				"		print(s);\r\n" + 
				"//		s.raiseStart();\r\n" + 
				"//		s.runCycle();\r\n" + 
				"//		System.in.read();\r\n" + 
				"//		s.raiseWhite();\r\n" + 
				"//		s.runCycle();\r\n" + 
				"//		print(s);\r\n" + 
				"		\r\n" + 
				"		\r\n" + 
				"		Scanner scanner = new Scanner(System.in);\r\n" + 
				"		try {\r\n" + 
				"			String cmd;\r\n" + 
				"			while (scanner.hasNext()) {\r\n" + 
				"				cmd = scanner.nextLine();\r\n" + 
				"				readFromConsole(cmd, s);\r\n" + 
				"			}\r\n" + 
				"		} finally {\r\n" + 
				"			scanner.close();\r\n" + 
				"			\r\n" + 
				"		}\r\n" + 
				"		\r\n" + 
				"	}\r\n" + 
				"\r\n";
		
		String beginning = "public static void print(IExampleStatemachine s) {\n";
		String middle = "";
		for (int i = 0; i < scInterface.get(0).size(); i++) {
			middle += String.format("\tSystem.out.println(\"%c = \" + s.getSCInterface().get%s());\n",
					Character.toUpperCase(scInterface.get(0).get(i).charAt(0)), 
					Character.toUpperCase(scInterface.get(0).get(i).charAt(0)) + scInterface.get(0).get(i).substring(1));
		}
		String end = "}";
		
		String beginSection = "	\r\n" + 
				"	private static void readFromConsole(String cmd, ExampleStatemachine s) {\r\n" + 
				"		switch(cmd) {\r\n"; 
		List<String> events = scInterface.get(1);
		String middleSection = "";
		for (int i = 0; i < events.size(); i++) {
			middleSection += String.format(""
					+ "			case \"%s\":\r\n" + 
					"				s.raise%s();\r\n" + 
					"				s.runCycle();\r\n" + 
					"				print(s);\r\n" + 
					"				break;\r\n", 
					events.get(i),
					Character.toUpperCase(events.get(i).charAt(0)) + events.get(i).substring(1));
		}
		
		String endSection = 
				"			case \"exit\":\r\n" + 
				"				s.exit();\r\n" + 
				"				s.runCycle();\r\n" + 
				"				print(s);\r\n" + 
				"				System.exit(0);\r\n" + 
				"				break;\r\n" + 
				"			default:\r\n" + 
				"				break;\r\n" + 
				"		}\r\n" + 
				"	}\r\n" + 
				"}\r\n";
		
		return mainClass + beginning + middle + end + beginSection + middleSection + endSection;
	}

	
}
