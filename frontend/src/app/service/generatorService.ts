import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { map } from 'rxjs/operators';
import {Observable} from "rxjs";
@Injectable({
  providedIn: 'root'
})

export class GeneratorService {
  generateUrl: string = "http://localhost:8080/generate/";
  loadUrl: string = "http://localhost:8080/load-ontology";
  ontologyUrl = "http://localhost:8080/ontologyTEST";

  constructor(private http: HttpClient) {
  }

  generateOntology( apikey: string,prompt: string) {
    return this.http.post<Array<String>>(this.generateUrl+apikey, prompt);
  }

  getOntologyTEST(): Observable<cytoscape.ElementDefinition[]> {
    return this.http.get(this.ontologyUrl)
      .pipe(
        map((ontology: any) => this.convertOntologyToElements(ontology))
      );
  }

  private convertOntologyToElements(ontology: any): cytoscape.ElementDefinition[] {
    const elements: cytoscape.ElementDefinition[] = [];

    // Iterate over the classes in the ontology and create nodes
    ontology.classes.forEach((cls: any) => {
      const node: cytoscape.NodeDefinition = {
        data: {
          id: cls.id,
          name: cls.label
        },
        classes: 'class'
      };
      elements.push(node);

      // Iterate over the subclass relationships and create edges
      cls.subclasses.forEach((subcls: any) => {
        const edge: cytoscape.EdgeDefinition = {
          data: {
            source: cls.id,
            target: subcls.id,
            name: 'is a'
          },
          classes: 'subclass'
        };
        elements.push(edge);
      });
    });

    return elements;
  }

  // generateOntology(prompt: string) {
  //   return this.http.post<Array<String>>(this.generateUrl, prompt);
  // }

  loadOntology() {
    return this.http.get<Array<String>>(this.loadUrl);
  }

}
