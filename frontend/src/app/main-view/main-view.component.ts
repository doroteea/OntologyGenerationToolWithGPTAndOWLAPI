import {Component, Input, ElementRef, ViewChild, OnInit, HostListener} from '@angular/core';

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
import PropertyValue = cytoscape.Css.PropertyValue;
import { Node } from 'src/app/service/Node';
import { Edge } from 'src/app/service/Edge';

interface GraphData {
  nodes: Node[];
  edges: Edge[];
}

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
  cyst!: any;

  constructor(private service: GeneratorService, private ontologyService: OntologyService, private http: HttpClient) {
  }

  ngOnInit(): void {
    this.isFileSelected = false;

    // const url = 'https://service.tib.eu/webvowl/';
    // window.open(url, '_blank');
  }

  @HostListener('window:beforeunload', ['$event'])
  unloadNotification($event: BeforeUnloadEvent) {
    if (!this.canReload()) {
      $event.preventDefault();
      $event.returnValue = '';
    }
  }

  canReload(): boolean {
    return confirm('Are you sure you want to reload the website?');
  }

  ontologyFile: any;

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

  // async getGraph() {
  //   const response = await fetch('http://localhost:8080/ontology');
  //   const owlText = await response.text();
  //   console.log(owlText);
  //   const ontology = new Ontology(owlText);
  //   const elements = [
  //     ...ontology.getClasses().map((clazz: { id: any; label: any; }) => ({ data: { id: clazz.id, label: clazz.label } })),
  //     ...ontology.getObjectProperties().map((prop: { id: any; label: any; source: { id: any; }; target: { id: any; }; }) => ({ data: { id: prop.id, label: prop.label, source: prop.source.id, target: prop.target.id }, classes: 'edge' })),
  //   ];
  //   console.log(elements);
  //   cytoscape({
  //     container: document.getElementById('cy'),
  //     elements,
  //     style: [
  //       {
  //         selector: 'node[label]',
  //         style: {
  //           'background-color': '#2e3e50',
  //           'label': 'data(id)',
  //           'text-valign': 'center',
  //           'text-halign': 'center',
  //           'text-wrap': 'wrap',
  //           'border-color': '#fff',
  //           'shape': 'ellipse',
  //           'text-outline-color': '#000',
  //           'text-outline-width': '1px',
  //           'color': '#fff',
  //           'font-size': '8px',
  //           'text-opacity': 1,
  //           'background-opacity': 1,
  //           'background-image-opacity': 0,
  //           'z-index': 10,
  //           'width': function(ele: { data: (arg0: string) => { (): any; new(): any; length: number; }; }) {
  //             return (ele.data('id').length*6) + 'px';
  //           },
  //           'height': function(ele) {
  //             return (ele.data('id').length*6) + 'px';
  //           }
  //         }
  //
  //       },
  //       {
  //         selector: 'edge',
  //         style: {
  //           'color': '#fff',
  //           'curve-style': 'bezier',
  //           'target-arrow-shape': 'triangle',
  //           'line-color': '#7f8c8d',
  //           'target-arrow-color': '#7f8c8d',
  //           'label': 'data(label)',
  //           'font-size': '10px',
  //           'text-outline-width': 2,
  //           'text-outline-color': '#7f8c8d',
  //           'width': 1
  //         }
  //       }
  //     ],
  //     layout: {
  //       name: 'dagre'
  //     }
  //   });
  //
  // }

  getGraph() {
    this.http.get<GraphData>('http://localhost:8080/graph').subscribe(response => {
      const nodes = response.nodes.map(node => ({ data: { id: node.id } }));
      const edges = response.edges.map(edge => ({ data: { id: edge.id, source: edge.source_id, target: edge.target_id } }));

      this.cyst = cytoscape({
        container: document.getElementById('cy'),
        style: [
          {
            selector: 'node::hover',
            style:{
              'background-color': 'cornflowerblue',
            },
          },
          {
            selector: 'node',
            style: {
              'font-weight': 'bold',
              'background-color': 'lightblue',
              'label': 'data(id)',
              'text-halign': 'center',
              'text-valign': 'center',
              'font-size' : '12px',
              'color': 'black',
              'width': function(ele: { data: (arg0: string) => { (): any; new(): any; length: number; }; }) {
                return (ele.data('id').length*7) + 'px';
              },
              'height': function(ele) {
                return (ele.data('id').length*7) + 'px';
              },
              'shape': 'ellipse',
            }
          },
          {
            selector: 'edge',
            style: {
              'font-weight': 'bold',
              'font-size':'15px',
              'curve-style': 'bezier',
              'target-arrow-shape': 'triangle',
              'label': 'data(id)',
              'line-color': 'lightblue',
              'target-arrow-color': '#ccc',
              'target-arrow-fill': 'filled'
            }
          }
        ],
        elements: [
          ...nodes,
          ...edges
        ],
        layout: {
          name: 'cose'
        }
      });
    });

  }

  focusGraph() {
    const cyDiv = document.getElementById("cy") as HTMLDivElement;
    if (cyDiv) {
      cyDiv.hidden = false;
    }
    const container = this.cyst.container();
    const rect = container.getBoundingClientRect();
    const center = {
      x: rect.width / 3,
      y: rect.height / 4,
    };
   this.cyst.animate({
      pan: center,
      zoom: 1
    }, {
      duration: 1000
    });
  }

  validateOntology() {
    this.service.validateOntology().subscribe((data: any) => {
      alert(data);
    });
  }

  visualize() {
    const cyDiv = document.getElementById("cy") as HTMLDivElement;
    if (cyDiv) {
      cyDiv.hidden = false;
    }
    this.getGraph();
  }

  openai() {
    const url = 'https://beta.openai.com/signup';
    window.open(url, '_blank');
  }
}
