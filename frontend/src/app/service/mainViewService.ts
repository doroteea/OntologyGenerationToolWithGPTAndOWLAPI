import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import {BaseMetrics} from "../model/BaseMetrics";
import {ClassAxiomsMetrics} from "../model/ClassAxiomsMetrics";
import {DataPropertyAxiomsMetrics} from "../model/DataPropertyAxiomsMetrics";
import {ObjectPropertyAxiomsMetrics} from "../model/ObjectPropertyAxiomsMetrics";
import {IndividualAxiomsMetrics} from "../model/IndividualAxiomsMetrics";


@Injectable({
  providedIn: 'root'
})

export class MainViewService {
  generateUrl: string = "http://localhost:8080/generate/";
  ontologyUrl: string =  "http://localhost:8080/ontology";
  validateUrl: string = "http://localhost:8080/validator";
  convertUrl: string = "http://localhost:8080/convert";
  basicmetricsUrl: string = "http://localhost:8080/metrics";
  classmetricsUrl: string = "http://localhost:8080/class-axioms-metrics";
  individualmetricsUrl: string = "http://localhost:8080/individual-axioms-metrics";
  objectmetricsUrl: string = "http://localhost:8080/object-property-metrics";
  datametricsUrl: string = "http://localhost:8080/data-property-metrics";


constructor(private http: HttpClient) {}

  tripletsToOntology(text: string) {
    return this.http.post<Array<String>>(this.ontologyUrl,text);
  }

  generateOntology(apikey:string, prompt:string){
      return this.http.post<Array<String>>(this.generateUrl + apikey, prompt);
  }

   validateOntology(text:string) {
      return this.http.post<Array<String>>(this.validateUrl,text);
  }

  convertOntology(syntax: string, onto:string){
      return this.http.post<Array<String>>(this.convertUrl+"/"+syntax,onto);
  }

  getBaseMetrics(onto: string) {
    return this.http.post<BaseMetrics>(this.basicmetricsUrl,onto);
  }

  getClassMetrics(onto: string) {
    return this.http.post<ClassAxiomsMetrics>(this.classmetricsUrl,onto);
  }

  getDataMetrics(onto: string) {
    return this.http.post<DataPropertyAxiomsMetrics>(this.datametricsUrl,onto);
  }

  getObjectMetrics(onto: string) {
    return this.http.post<ObjectPropertyAxiomsMetrics>(this.objectmetricsUrl,onto);
  }

  getIndividualMetrics(onto: string) {
    return this.http.post<IndividualAxiomsMetrics>(this.individualmetricsUrl,onto);
  }

}
