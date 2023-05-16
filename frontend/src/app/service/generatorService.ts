import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { map } from 'rxjs/operators';
import {Observable} from "rxjs";
import {Node} from "./Node";
import {Edge} from "./Edge";

interface GraphData {
  nodes: Node[];
  edges: Edge[];
}

@Injectable({
  providedIn: 'root'
})

export class GeneratorService {
  generateUrl: string = "http://localhost:8080/generate/";
  validateUrl: string = "http://localhost:8080/validate";
  loadUrl: string = "http://localhost:8080/load-ontology";
  graphUrl: string = "http://localhost:8080/graph";
  convertUrl: string = "http://localhost:8080/convert";


constructor(private http: HttpClient) {}

  generateGraph(text: string) {
    console.log("here");
    console.log(text);
    return this.http.post<GraphData>(this.graphUrl,text);
  }

  generateOntology(apikey:string, prompt:string){
      return this.http.post<Array<String>>(this.generateUrl + apikey, prompt);
  }

   validateOntology(text:string) {
      return this.http.post<Array<String>>(this.validateUrl,text);
  }

  loadOntology() {
      return this.http.get<Array<String>>(this.loadUrl);
  }

  convertOntology(syntax: string, onto:string){
      return this.http.post<Array<String>>(this.convertUrl+"/"+syntax,onto);
  }

}
