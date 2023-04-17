import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})

export class GeneratorService {
  generateUrl: string = "http://localhost:8080/generate";
  loadUrl: string = "http://localhost:8080/load-ontology";

  constructor(private http: HttpClient) {
  }

  generateOntology(prompt: string, apikey: string) {
    return this.http.post<Array<String>>(this.generateUrl+apikey, prompt);
  }

  // generateOntology(prompt: string) {
  //   return this.http.post<Array<String>>(this.generateUrl, prompt);
  // }

  loadOntology() {
    return this.http.get<Array<String>>(this.loadUrl);
  }

}
