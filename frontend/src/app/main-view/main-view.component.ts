import {Component, Input, ElementRef, ViewChild, OnInit} from '@angular/core';

import * as cytoscape from 'cytoscape';
import * as rdf from 'rdflib';
import * as cydagre from 'cytoscape-dagre';
import * as vis from 'vis';
import * as $rdf from 'rdflib';
// import * as edgehandles from 'cytoscape-edgehandles';
import * as dagre from 'cytoscape-dagre';


import {GeneratorService} from '../service/generatorService';
import {OntologyService} from "../service/ontologyService";
import {HttpClient} from "@angular/common/http";
import {EdgeDefinition, NodeDefinition} from "cytoscape";
import {Ontology} from "../service/Ontology";

cytoscape.use(dagre);
@Component({
  selector: 'app-main-view',
  templateUrl: './main-view.component.html',
  styleUrls: ['./main-view.component.css'],
})
export class MainViewComponent implements OnInit {
  @Input() onto!: string;
  @ViewChild('cy') cy!: ElementRef;

  selectedFile: File | null = null;
  isFileSelected = false;
  fileContent: string = '';
  base_ontology!: string;
  graph: any;
  private readonly ONTOLOGY_URL = 'http://localhost:8080/ontologyTEST';
  private ontologyUrl = 'http://localhost:8080/ontology';

  constructor(private service: GeneratorService, private ontologyService: OntologyService, private http: HttpClient) {
  }

  ngOnInit(): void {
    this.isFileSelected = false;

    // const url = 'https://service.tib.eu/webvowl/';
    // window.open(url, '_blank');
    // this.getGraph();
    this.getGraph();
  }

// Function to upload the OWL file and submit the form to WebVOWL
  openWebVOWL(file: File) {
      const form = document.createElement('form');
      form.method = 'POST';
      form.action = 'https://service.tib.eu/webvowl/#file=ontology.owl';

      const formData = new FormData();
      formData.append('uploadFile', file);

      const input = document.createElement('input');
      input.type = 'hidden';
      input.name = 'data';
      input.value = JSON.stringify({ viewMode: 'vowl', ontologyFormat: 'RDF/XML' });

      form.appendChild(input);
      form.appendChild(document.createElement('br'));

      document.body.appendChild(form);
      form.submit();
    }

  ontologyFile: any;

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.openWebVOWL(file);
    }
  }

  // async getGraph() {
  //   const response = await fetch('http://localhost:8080/ontology');
  //   const owlText = await response.text();
  //   console.log(owlText);
  //
  //   const url = 'http://localhost:3000/webvowl';
  //   const options = {
  //     headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  //     responseType: 'text' as const
  //   };
  //   const data = new URLSearchParams();
  //   data.set('ontology', owlText);
  //
  //   this.http.get(url+"/#file=ontology.owl").subscribe(response => {
  //       console.log(response);
  //       // const graphData = JSON.parse(response);
  //       // this.displayGraph(graphData);
  //
  //   });
  // }
  //
  // displayGraph(graphData: any) {
  //   const container = document.getElementById('cy');
  //   this.graph = cytoscape({
  //     container,
  //     elements: graphData,
  //     style: [
  //       {
  //         selector: 'node',
  //         style: {
  //           'background-color': '#666',
  //           'label': 'data(name)'
  //         }
  //       },
  //       {
  //         selector: 'edge',
  //         style: {
  //           'curve-style': 'bezier',
  //           'target-arrow-shape': 'triangle',
  //           'line-color': '#ccc',
  //           'target-arrow-color': '#ccc',
  //           'label': 'data(label)'
  //         }
  //       }
  //     ]
  //   });
  // }

  // private loadOntologyTEST(): void {
  //   this.service.getOntologyTEST()
  //     .subscribe((elements: cytoscape.ElementDefinition[]) => {
  //       this.cy.add(elements);
  //       this.cy.layout({ name: 'dagre' }).run();
  //     });
  // }
  //
  // private initializeGraph(): void {
  //   cytoscape.use(cydagre);
  //   const container = document.getElementById('cy');
  //   this.cy = cytoscape({
  //     container,
  //     style: [
  //       {
  //         selector: 'node',
  //         style: {
  //           'label': 'data(name)',
  //           'text-valign': 'center',
  //           'text-halign': 'center',
  //           'color': 'white',
  //           'background-color': 'gray'
  //         }
  //       },
  //       {
  //         selector: 'edge',
  //         style: {
  //           'label': 'data(name)',
  //           'curve-style': 'bezier',
  //           'target-arrow-shape': 'triangle',
  //           'line-color': 'gray',
  //           'target-arrow-color': 'gray',
  //           'width': 2
  //         }
  //       },
  //       {
  //         selector: '.class',
  //         style: {
  //           'background-color': 'blue'
  //         }
  //       },
  //       {
  //         selector: '.subclass',
  //         style: {
  //           'line-style': 'dashed'
  //         }
  //       }
  //     ],
  //     layout: {
  //       name: 'dagre'
  //     }
  //   });
  // }


  // onFileSelected(event: any) {
  //   const file: File = event.target.files[0];
  //   const reader: FileReader = new FileReader();
  //   reader.readAsText(file);
  //   reader.onload = (e) => {
  //     // @ts-ignore
  //     this.fileContent = reader.result.toString();
  //     console.log(this.fileContent.toString());
  //     this.isFileSelected = true;
  //   };
  // }

  generateFunction(value: string) {
    const apiKeyInput = document.getElementById('apikey') as HTMLInputElement;
    const apiKeyValue = apiKeyInput.value;
    console.log(apiKeyValue); // logs the value inside the apiKeyInput

    if (this.isFileSelected) {
      value += this.fileContent.toString();
      value += this.base_ontology;
      console.log(value);
    }
    if(apiKeyValue == "" || apiKeyValue == null){
      alert("Insert your api key")
    } else {
      const deactivateBtn = document.getElementById(
        'deactivateBtn'
      ) as HTMLButtonElement;
      const myDiv = document.getElementById('myDiv') as HTMLDivElement;
      myDiv.style.opacity = '0.5'; // set the opacity to make it look deactivated
      myDiv.style.pointerEvents = 'none'; // disable pointer events to prevent user interaction
      this.showImage();

      this.service.generateOntology(apiKeyValue, value).subscribe((data: any) => {
        console.log(data);
        console.log(data[0]);
        this.onto = data[0];
        this.base_ontology = data[0];
        myDiv.style.opacity = '1'; // reset the opacity
        myDiv.style.pointerEvents = 'auto'; // enable pointer events
        this.hideImage();
      });
    }
  }

  // generateFunction(value: string) {
  //   if (this.isFileSelected) {
  //     value += this.fileContent.toString();
  //   }
  //   console.log(value);
  //   const deactivateBtn = document.getElementById(
  //     'deactivateBtn'
  //   ) as HTMLButtonElement;
  //   const myDiv = document.getElementById('myDiv') as HTMLDivElement;
  //   myDiv.style.opacity = '0.5'; // set the opacity to make it look deactivated
  //   myDiv.style.pointerEvents = 'none'; // disable pointer events to prevent user interaction
  //   this.showImage();
  //
  //   this.service.generateOntology(value).subscribe((data: any) => {
  //     console.log(data);
  //     console.log(data[0]);
  //     this.onto = data[0];
  //     myDiv.style.opacity = '1'; // reset the opacity
  //     myDiv.style.pointerEvents = 'auto'; // enable pointer events
  //     this.hideImage();
  //   });
  // }

  deactivate(element: HTMLElement) {
    // Add a disabled attribute to the element
    element.setAttribute('disabled', 'true');

    // Apply a "disabled" class to the element
    element.classList.add('disabled');

    // Disable any child elements of the given element
    const childElements = element.querySelectorAll(
      '*'
    ) as NodeListOf<HTMLElement>;
    childElements.forEach((childElement) => {
      childElement.setAttribute('disabled', 'true');
      childElement.classList.add('disabled');
    });
  }

  activate() {
    const elements = document.querySelectorAll(
      'button, input, select, textarea'
    ) as NodeListOf<
      HTMLInputElement | HTMLButtonElement | HTMLSelectElement | HTMLTextAreaElement
      >;
    for (let i = 0; i < elements.length; i++) {
      const el = elements[i];
      el.disabled = false;
    }
    const overlay = document.getElementById('overlay');
    if (overlay) {
      overlay.style.display = 'none';
    }
  }

  showImage(): void {
    const img = document.getElementById('myImg') as HTMLImageElement;
    img.style.visibility = 'visible';
  }

  hideImage(): void {
    const img = document.getElementById('myImg') as HTMLImageElement;
    img.style.visibility = 'hidden';
  }

  loadOntology() {
    this.service.loadOntology().subscribe(
      (data: any) => {
        // handle the response data here
        console.log(data);
      },
      (error: any) => {
        // handle any errors here
        console.error(error);
      }
    );
  }

  resetFileInput() {
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    fileInput.value = '';
    this.fileContent = '';
    this.isFileSelected = false;
  }

  async getGraph() {
    const response = await fetch('http://localhost:8080/ontology');
    const owlText = await response.text();
    console.log(owlText);
    const ontology = new Ontology(owlText);
    const elements = [
      ...ontology.getClasses().map((clazz: { id: any; label: any; }) => ({ data: { id: clazz.id, label: clazz.label } })),
      ...ontology.getObjectProperties().map((prop: { id: any; label: any; source: { id: any; }; target: { id: any; }; }) => ({ data: { id: prop.id, label: prop.label, source: prop.source.id, target: prop.target.id }, classes: 'edge' })),
    ];
    console.log(elements);

    cytoscape({
      container: document.getElementById('cy'),
      elements,
      style: [
        {
          selector: 'node[label]',
          style: {
            'background-color': '#2e3e50',
            'label': 'data(label)',
            'text-wrap': 'wrap',
            'text-valign': 'center',
            'text-halign': 'center',
            'font-size': '10px',
            'width': 'label',
            'height': 'label',
            'shape': 'rectangle'
          }
        },
        {
          selector: 'edge',
          style: {
            'curve-style': 'bezier',
            'target-arrow-shape': 'triangle',
            'line-color': '#7f8c8d',
            'target-arrow-color': '#7f8c8d',
            'label': 'data(label)',
            'font-size': '10px',
            'text-outline-width': 2,
            'text-outline-color': '#7f8c8d',
            'width': 1
          }
        }
      ],
      layout: {
        name: 'dagre'
      }
    });

  }
}
