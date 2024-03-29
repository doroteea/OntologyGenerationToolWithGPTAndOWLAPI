import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';

import * as cytoscape from 'cytoscape';
import {MainViewService} from '../service/mainViewService';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import { saveAs } from 'file-saver';
import {BaseMetrics} from "../model/BaseMetrics";
import {ClassAxiomsMetrics} from "../model/ClassAxiomsMetrics";
import {ObjectPropertyAxiomsMetrics} from "../model/ObjectPropertyAxiomsMetrics";
import {DataPropertyAxiomsMetrics} from "../model/DataPropertyAxiomsMetrics";
import {IndividualAxiomsMetrics} from "../model/IndividualAxiomsMetrics";
import Swal from 'sweetalert2';
import {response} from "express";

@Component({
  selector: 'app-main-view',
  templateUrl: './main-view.component.html',
  styleUrls: ['./main-view.component.css'],
})

export class MainViewComponent implements OnInit {
  @Input() onto!: string;
  @Input() validationReport!: string;
  @ViewChild('cy') cyContainer!: ElementRef;

  cy: cytoscape.Core | null = null;

  num: string = '1';

  //conversion
  ontologyFormatsConverted: string[]= ["OWL", "RDF", "TURTLE","Manchester Syntax","Functional Syntax", "KRSS2"];
  selectedOntologySyntax!: string;
  converted: any;

  //metrics management
  baseMetrics!: BaseMetrics;
  classMetrics!: ClassAxiomsMetrics;
  objectMetrics!: ObjectPropertyAxiomsMetrics;
  dataMetrics!: DataPropertyAxiomsMetrics;
  individualMetrics!: IndividualAxiomsMetrics;

  //file management
  selectedFile: File | null = null;
  isFileSelected = false;
  fileContent: string = '';
  ontologyFileContent: string = '';
  ontologyFile: any;

  constructor(private service: MainViewService, private httpClient: HttpClient) {}

  ontology_list! : any[];
  updates_list! : any[];
  SELECTED_PROMPT = "STATELESS";

  prompt = '';
  buttonClass = '';
  buttonText = '';

  ngOnInit(): void {
    this.SELECTED_PROMPT = "STATELESS";
    this.buttonClass = 'button';
    this.buttonText = 'RDF Schema';
    this.selectedOntologySyntax = this.ontologyFormatsConverted[0];
  }

  convert(syntax: string) {
    console.log("here bish");
    console.log(syntax);
    let formattedSyntax: string;
    switch (syntax) {
      case 'OWL':
        formattedSyntax = 'owlxml';
        break;
      case 'RDF':
        formattedSyntax = 'rdfxml';
        break;
      case 'TURTLE':
        formattedSyntax = 'ttl';
        break;
      case 'Manchester Syntax':
        formattedSyntax = 'manc';
        break;
      case 'Functional Syntax':
        formattedSyntax = 'func';
        break;
      case 'KRSS2':
        formattedSyntax = 'krss2';
        break;
      default:
        formattedSyntax = 'txt';
        break;
    }
    console.log(formattedSyntax);
    this.service.convertOntology(formattedSyntax, this.onto).subscribe((data: any) => {
      console.log(data);
      console.log(data[0]);
      this.converted = data[0];
    });
  }

  updateNum(s: string) {
    this.num = s; // then change num value

    switch (s) {
      case "1":
        this.num = '1';
        break;
      case "2":
        this.num = '2';
        break;
      case "3":
        this.num = '3';
        break;
      case "4":
        this.num = '4';
        console.log("here");
        this.performMetricsRequest();
        break;
      case "5":
        console.log("wtf");
        this.performValidation();
        this.num = '5';
        break;
      case "6":
        this.num = '6';
        break;
      default:
        this.num = '1';
        break;
    }
  this.updateCystoGraph();
  }

  updateCystoGraph() {
    if (this.buttonText == "RDF Schema") {
      //  console.log("rdf graph"+ this.graphStateRDF);
      // @ts-ignore

// Filter and map the graphState
      let filteredEdges = this.graphState.edges.filter(edge => !edge.label.includes('rdf'));
      // @ts-ignore
      let nodeIds = new Set(filteredEdges.flatMap(edge => [edge.from, edge.to]));
      // @ts-ignore
      let elements = [
        ...this.graphState.nodes
          // @ts-ignore
          .filter(node => nodeIds.has(node.id))
          // @ts-ignore
          .map(node => ({ data: { id: node.id, label: node.label, color: node.color } })),
        ...filteredEdges
          // @ts-ignore
          .map(edge => ({ data: { id: 'e' + edge.from + '-' + edge.to, source: edge.from, target: edge.to, label: edge.label } })),
      ];
      // @ts-ignore
      this.cy = cytoscape({
        container: document.getElementById('cy'),
        elements: elements,
        style: [
          {
            selector: 'node',
            style: {
              'label': 'data(label)',
              'background-color': 'data(color)',
              'border-color': '#000',
              'border-width': '1px',
              'width': '50px',
              'height': '50px'
            }
          },
          {
            selector: 'edge',
            style: {
              'label': 'data(label)',
              'curve-style': 'bezier',
              'target-arrow-shape': 'triangle',
              'target-arrow-color': '#000',
              'line-color': '#000',
              'width': '1px'
            }
          }
        ]
      });
    } else {
      console.log("graph state");
      console.log(this.graphState);
      //@ts-ignore
      this.cy = cytoscape({
        container: document.getElementById('cy'),
        elements: [
          // @ts-ignore
          ...(this.graphState.nodes.map(node => ({data: {id: node.id, label: node.label, color: node.color}}))),
          // @ts-ignore
          ...(this.graphState.edges.map(edge => ({
            data: {
              // @ts-ignore
              id: edge.id,
              // @ts-ignore
              source: edge.from,
              // @ts-ignore
              target: edge.to,
              // @ts-ignore
              label: edge.label
            }
          })))
        ],
        style: [
          {
            selector: 'node',
            style: {
              'label': 'data(label)',
              'background-color': 'data(color)',
              'border-color': '#000',
              'border-width': '1px',
              'width': '50px',
              'height': '50px'
            }
          },
          {
            selector: 'edge',
            style: {
              'label': 'data(label)',
              'curve-style': 'bezier',
              'target-arrow-shape': 'triangle',
              'target-arrow-color': '#000',
              'line-color': '#000',
              'width': '1px'
            }
          }
        ]
      });
    }
  }

  updateGraph(updates: any[]) {
    let current_graph = JSON.parse(JSON.stringify(this.graphState));

    if (updates.length === 0) {
      return;
    }
    if (typeof updates[0] === "string") {
      updates = [updates];
    }
    updates.forEach(update => {
      if (update.length === 3) {
        const [entity1, relation, entity2] = update;
        console.log(this.buttonText + " and relation: " + relation);

        if (this.buttonText === "RDF Schema" && relation.includes('rdf')) {
          return;
        }
        //finding whether the nodes already exist in the current graph
        let node1 = current_graph.nodes.find((node: { id: any; }) => node.id === entity1);
        let node2 = current_graph.nodes.find((node: { id: any; }) => node.id === entity2);

        //if they don't we save them into the graph
        if (node1 === undefined) {
          current_graph.nodes.push({ id: entity1, label: entity1, color: "#6495ed"});
        }

        if (node2 === undefined) {
          current_graph.nodes.push({ id: entity2, label: entity2, color: "#6495ed"});
        }

        let edge = current_graph.edges.find((edge: { from: any; to: any; }) => edge.from === entity1 && edge.to === entity2);
        if (edge !== undefined) {
          edge.label = relation;
          return;
        }

        current_graph.edges.push({ from: entity1, to: entity2, label: relation});

      }
      else if (update.length === 2 && update[1].startsWith("#")) {

        const [entity, color] = update;

        var node = current_graph.nodes.find((node: { id: any; }) => node.id === entity);

        if (node === undefined) {
          current_graph.nodes.push({id: entity, label: entity, color: color});
          return;
        }

        node.color = color;
        console.log(color);
      }
      else if (update.length === 2 && update[0] === "DELETE") {
        const [_, index] = update;

        let node = current_graph.nodes.find((node: { id: any; }) => node.id === String(index) || node.id === index);

        if (node === undefined) {
          return;
        }

        current_graph.nodes = current_graph.nodes.filter((node: { id: any; }) => node.id !== index);
        current_graph.edges = current_graph.edges.filter((edge: { from: any; to: any; }) => edge.from !== index && edge.to !== index);

      }
    });

    this.graphState = current_graph;

    this.updateCystoGraph();

    document.body.style.cursor = "auto";
  }

  onOntologySelected(event: any) {
    const file1: File = event.target.files[0];
    const reader: FileReader = new FileReader();
    reader.readAsText(file1);
    reader.onload = (e) => {
      // @ts-ignore
      this.ontologyFileContent = reader.result.toString();
      console.log(this.ontologyFileContent.toString());
      this.onto = this.ontologyFileContent.toString();
    };
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    const reader: FileReader = new FileReader();
    reader.readAsText(file);
    reader.onload = (e) => {
      // @ts-ignore
      this.fileContent = reader.result.toString();
      console.log(this.fileContent.toString());
      this.isFileSelected = true;
    };
  }

  openai() {
  const url = 'https://beta.openai.com/signup';
  window.open(url, '_blank');
}

  async stateless(prompt: string, apiKey: string) {
    try {
      const response = await this.httpClient.get('assets/prompt/stateless.prompt', { responseType: 'text' }).toPromise();
      // @ts-ignore
      const promptText = response.replace("$prompt", prompt);
      console.log(promptText);

      this.service.generateOntology(apiKey, promptText).subscribe((data: any[]) => {
        this.ontology_list = data;
        if (data.length > 0) {
          const updates = JSON.parse(data[0]);
          this.updates_list = updates;
          this.updateGraph(updates);
          this.SELECTED_PROMPT = "STATEFUL";
        } else {
          console.error('Invalid data format');
        }
        },
        (error) => {
          console.log(error);
          if (error.status === 500) {
            Swal.fire({
              icon: 'info',
              title: 'Wrong OpenAI API key',
              text: 'Check your API key again',
              confirmButtonText: 'Okay',
              confirmButtonColor: '#3085d6',
            })
            document.body.style.cursor = 'auto';
          } else {
            // Handle other errors
            alert('An error occurred: ' + error.message);
          }
        }
      );
    } catch (error) {
      console.log(error);
      alert(error);
    }
  }

  async stateful(prompt: string, apiKey: string) {
    try {
      const response = await this.httpClient.get('assets/prompt/stateful.prompt', { responseType: 'text' }).toPromise();
      // @ts-ignore

      const promptText = response.replace("$prompt", prompt)
        // @ts-ignore
        .replace("$state", this.ontology_list);
      console.log(promptText);

      this.service.generateOntology(apiKey, promptText).subscribe((data: any[]) => {
        this.ontology_list = data;
        if (data.length > 0) {
          console.log(data);
          const updates = JSON.parse(data[0]);
          this.updates_list = updates;
          this.updateGraph(updates);
        } else {
          console.error('Invalid data format');
        }
        },
        (error) => {
          console.log(error);
          if (error.status === 500) {
            Swal.fire({
              icon: 'info',
              title: 'Wrong OpenAI API key',
              text: 'Check your API key again',
              confirmButtonText: 'Okay',
              confirmButtonColor: '#3085d6',
            })
            document.body.style.cursor = 'auto';
          } else {
            // Handle other errors
            alert('An error occurred: ' + error.message);
          }
        }
      );
    } catch (error) {
      console.log(error);
      alert(error);
    }
  }

  async queryPrompt(prompt: string, apiKey: string) {
    if (this.SELECTED_PROMPT === "STATELESS") {
      await this.stateless(prompt, apiKey);
    } else if (this.SELECTED_PROMPT === "STATEFUL"){
      await this.stateful(prompt, apiKey);
    }else {
      alert("Please select a prompt");
    }
  }
  reloadPage() {
    location.reload();
  }

  clearState() {
    this.graphState = {
      nodes: [],
      edges: []
    };
    this.SELECTED_PROMPT = "STATELESS";


    this.updates_list = [];
    this.ontology_list = [];

    this.updateGraph([]);
    this.reloadPage();
  }

  generateFunction()  {
    const apiKeyInput = document.getElementById("apikey") as HTMLInputElement;
    if(apiKeyInput.value.trim().length > 0) {
      const apiKey = apiKeyInput.value;

      const prompt: string = (document.getElementsByClassName("abstract")[0] as HTMLInputElement).value;
      if(prompt.trim().length > 0){
        document.body.style.cursor = 'wait';
        this.queryPrompt(prompt, apiKey);
      } else {
        Swal.fire({
          icon: 'info',
          title: 'Prompt Required',
          text: 'Please load an abstract or write a prompt.',
          confirmButtonText: 'Okay',
          confirmButtonColor: '#3085d6',
        })
      }
    } else {
      Swal.fire({
        icon: 'info',
        title: 'API Key Required',
        text: 'Please insert your OpenAI API key',
        confirmButtonText: 'Okay',
        confirmButtonColor: '#3085d6',
      });
    }

  }

  tripleToOntology() {
    const triplets = String(this.ontology_list);
    console.log("triplets: "+triplets);
    this.service.tripletsToOntology(triplets).subscribe((data: any) => {
            console.log("ontology:\n" + data[0]);
            this.onto = data[0];
          });
    this.num = '2';

  }

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type':  'text/plain',
      'Response-Type': 'json'
    })
  };

  performMetricsRequest() {
    console.log("hello world");
    if (this.onto) {
      this.service.getBaseMetrics(this.onto).subscribe(response => {
        console.log("Response: " + JSON.stringify(response));
        this.baseMetrics = response;
      }, error => {
        console.error('Error:', error);
      });

      this.service.getClassMetrics(this.onto).subscribe(response =>{
        console.log("Response: " + JSON.stringify(response));
        this.classMetrics = response;
      }, error => {
        console.error('Error:', error);
      });

      this.service.getObjectMetrics(this.onto).subscribe(response => {
        console.log("Response: " + JSON.stringify(response));
        this.objectMetrics = response;
      }, error => {
        console.error('Error:', error);
      });

      this.service.getDataMetrics(this.onto).subscribe(response => {
        console.log("Response: " + JSON.stringify(response));
        this.dataMetrics = response;
      }, error => {
        console.error('Error:', error);
      });

      this.service.getIndividualMetrics(this.onto).subscribe(response => {
        console.log("Response: " + JSON.stringify(response));
        this.individualMetrics = response;
      }, error => {
        console.error('Error:', error);
      });

    }
  }

  performValidation() {
    this.service.validateOntology(this.onto).subscribe((data: any) => {
      this.validationReport = this.formatValidationReport(data);
    });
  }

  formatValidationReport(report: string): string {
    if (report.includes("On statement: ")){
      const statements = report.split("On statement: ");
      let formattedReport = "";

      for (const statement of statements.slice(1)) {
        const [subject, predicate, ignored1, objectValue, ignored2] = statement.split(" ");

        const formattedStatement = `${subject} ${predicate}: ${objectValue}\n`;
        formattedReport += formattedStatement;
      }

      return formattedReport.trim();
    } else {
      return report;
    }

  }

  downloadFile() {
    if (this.onto) {
      let fileExtension = '';
      switch (this.selectedOntologySyntax) {
        case 'OWL':
          fileExtension = 'owl';
          break;
        case 'RDF':
          fileExtension = 'rdf';
          break;
        case 'TURTLE':
          fileExtension = 'ttl';
          break;
        case 'Manchester Syntax':
          fileExtension = 'ms';
          break;
        case 'Functional Syntax':
          fileExtension = 'fs';
          break;
        case 'KRSS2':
          fileExtension = 'krss2';
          break;
        default:
          fileExtension = 'txt';
          break;
      }
      const blob = new Blob([this.onto], {type: 'text/plain;charset=utf-8'});
      saveAs(blob, 'ontology.'+fileExtension);
    }
  }

  graphState = { nodes: [], edges: [] };
  changeGraph() {
    this.buttonClass = this.buttonClass === 'button' ? 'button_secundar' : 'button';
    this.buttonText = this.buttonText === 'RDF Schema' ? 'Knowledge Schema' : 'RDF Schema';

    this.updateGraph(this.updates_list);

    document.body.style.cursor = "auto";
  }



}
