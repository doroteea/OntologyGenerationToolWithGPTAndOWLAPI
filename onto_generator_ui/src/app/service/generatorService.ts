import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders, HttpResponse} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})

export class GeneratorService {
  generateUrl: string = "http://localhost:8080/generate";

  constructor(private http: HttpClient) {
  }

  generateOntology(prompt: string){
    console.log( this.http.post<HttpResponse<any>>(this.generateUrl ,prompt));
    return this.http.post<HttpResponse<Array<String>>>(this.generateUrl ,prompt);
  }

}
