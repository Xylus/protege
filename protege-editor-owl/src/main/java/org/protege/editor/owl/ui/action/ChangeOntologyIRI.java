package org.protege.editor.owl.ui.action;

import com.google.common.base.Optional;
import org.protege.editor.owl.ui.ontology.OntologyIDJDialog;
import org.semanticweb.owlapi.model.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 07-Mar-2007<br><br>
 */
public class ChangeOntologyIRI extends ProtegeOWLAction {
	private static final long serialVersionUID = -6080240335045735182L;


	public void actionPerformed(ActionEvent e) {
		OWLOntology ont = getOWLModelManager().getActiveOntology();
		OWLOntologyID id = OntologyIDJDialog.showDialog(getOWLEditorKit(), ont.getOntologyID());
		if (id != null) {
			getOWLModelManager().applyChanges(getChanges(ont, id));
		}
	}
	
    private List<OWLOntologyChange> getChanges(OWLOntology ontology, OWLOntologyID id) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        OWLOntologyManager owlOntologyManager = ontology.getOWLOntologyManager();
        OWLDataFactory factory = owlOntologyManager.getOWLDataFactory();
        OWLOntologyID oldId = ontology.getOntologyID();
        changes.add(new SetOntologyID(ontology, id));
        if (!id.isAnonymous() && !id.equals(oldId)) {
        	for (OWLOntology ont : owlOntologyManager.getOntologies()) {
        		for (OWLImportsDeclaration decl : ont.getImportsDeclarations()) {
        			if (Optional.of(decl.getIRI()).equals(oldId.getVersionIRI())) {
        				changes.add(new RemoveImport(ont, decl));
        				changes.add(new AddImport(ont, factory.getOWLImportsDeclaration(id.getDefaultDocumentIRI().get())));
        			}
        			else if (Optional.of(decl.getIRI()).equals(oldId.getOntologyIRI())) {
        				changes.add(new RemoveImport(ont, decl));
        				changes.add(new AddImport(ont, factory.getOWLImportsDeclaration(id.getOntologyIRI().get())));
        			}
        		}
        	}
        }
        return changes;
    }


    public void initialise() throws Exception {
    }


    public void dispose() throws Exception {
    }
}
