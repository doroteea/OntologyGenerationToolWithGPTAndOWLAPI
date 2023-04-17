import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OntologyService {
  private ontologyUrl = 'http://localhost:8080/ontology';

  constructor(private http: HttpClient) { }

  getOntologyFile(): Observable<Blob> {
    return this.http.get(this.ontologyUrl, { responseType: 'blob' });
  }
}
