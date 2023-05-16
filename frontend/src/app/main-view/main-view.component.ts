import {Component, ElementRef, HostListener, Input, OnInit, ViewChild} from '@angular/core';

import * as cytoscape from 'cytoscape';

import {GeneratorService} from '../service/generatorService';
import {HttpClient} from "@angular/common/http";
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-main-view',
  templateUrl: './main-view.component.html',
  styleUrls: ['./main-view.component.css'],
})

export class MainViewComponent implements OnInit {
  @Input() onto!: string;
  @Input() ontologyFormatsConverted: string[]= ["","OWL", "RDF", "TURTLE","Manchester Syntax","Functional Syntax", "KRSS2"];
  @ViewChild('cy') cy!: ElementRef;
  @ViewChild('pre') pre!: ElementRef;

  selectedFile: File | null = null;
  isFileSelected = false;
  fileContent: string = '';
  base_ontology!: string;
  ontologyFile: any;

  cyst!: any;

  constructor(private service: GeneratorService, private http: HttpClient) {
  }

  ngOnInit(): void {
    this.isFileSelected = false;
    this.convert();
  }

  convert(){
    const dropdown = document.getElementById("dropdown") as HTMLSelectElement;
    dropdown.addEventListener("change", (event) => {
      // @ts-ignore
      const selectedOption = event.target.value;
      console.log(selectedOption);
      this.service.convertOntology(selectedOption, this.onto).subscribe((data: any) => {
        console.log(data);
        console.log(data[0]);
        this.onto = data[0];
      });
    });
  }

  downloadFile() {
    const text = this.pre.nativeElement.textContent;
    const blob = new Blob([text], { type: 'text/plain;charset=utf-8' });
    saveAs(blob, 'ontology.owl');
  }

  // @HostListener('window:beforeunload', ['$event'])
  // unloadNotification($event: BeforeUnloadEvent) {
  //   if (!this.canReload()) {
  //     $event.preventDefault();
  //     $event.returnValue = '';
  //   }
  // }
  //
  // canReload(): boolean {
  //   return confirm('Are you sure you want to reload the website?');
  // }

  onOntologySelected(event: any) {
    const file1: File = event.target.files[0];
    const reader: FileReader = new FileReader();
    reader.readAsText(file1);
    reader.onload = (e) => {
      // @ts-ignore
      this.fileContent = reader.result.toString();
      console.log(this.fileContent.toString());
      this.onto = this.fileContent.toString();
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
    ) as NodeListOf<HTMLInputElement | HTMLButtonElement | HTMLSelectElement | HTMLTextAreaElement>;
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

  openai() {
  const url = 'https://beta.openai.com/signup';
  window.open(url, '_blank');
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
    if (apiKeyValue == "" || apiKeyValue == null) {
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

  resetFileInput() {
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    fileInput.value = '';
    this.fileContent = '';
    this.isFileSelected = false;
  }

  validateOntology() {
    const text = this.pre.nativeElement.textContent;
    this.service.validateOntology(text).subscribe((data: any) => {
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

  getGraph() {
    const text = this.pre.nativeElement.textContent;
    this.service.generateGraph(text).subscribe(response => {
      const nodes = response.nodes.map(node => ({data: {id: node.name, type: node.type}}));
      const edges = response.edges.map(edge => ({data: {id: edge.name, source: edge.domain, target: edge.range, type: edge.type}}));
      this.cyst = cytoscape({
        container: document.getElementById('cy'),
        style: [
          {
            selector: 'node[type="individual"]',
            style: {
              'font-weight': 'bold',
              'background-color': 'purple',
              'label': 'data(id)',
              'text-halign': 'center',
              'text-valign': 'center',
              'font-size': '12px',
              'color': 'black',
              'width': function (ele: { data: (arg0: string) => { (): any; new(): any; length: number; }; }) {
                return (ele.data('id').length * 7) + 'px';
              },
              'height': function (ele) {
                return (ele.data('id').length * 7) + 'px';
              },
              'shape': 'star',
            }
          },
          {
            selector: 'node[type="data_property"]',
            style: {
              'font-weight': 'bold',
              'background-color': 'lightgreen',
              'label': 'data(id)',
              'text-halign': 'center',
              'text-valign': 'center',
              'font-size': '12px',
              'color': 'black',
              'width': function (ele: { data: (arg0: string) => { (): any; new(): any; length: number; }; }) {
                return (ele.data('id').length * 7) + 'px';
              },
              'height': function (ele) {
                return (ele.data('id').length * 7) + 'px';
              },
              'shape': 'rectangle',
            }
          },
          {
            selector: 'node[type="concept"]',
            style: {
              'font-weight': 'bold',
              'background-color': 'lightblue',
              'label': 'data(id)',
              'text-halign': 'center',
              'text-valign': 'center',
              'font-size': '12px',
              'color': 'black',
              'width': function (ele: { data: (arg0: string) => { (): any; new(): any; length: number; }; }) {
                return (ele.data('id').length * 7) + 'px';
              },
              'height': function (ele) {
                return (ele.data('id').length * 7) + 'px';
              },
              'shape': 'ellipse',
            }
          },
          {
            selector: 'edge',
            style: {
              'font-weight': 'bold',
              'font-size': '15px',
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

  // getGraph() {
  //   const text = this.pre.nativeElement.textContent;
  //   this.service.generateGraph(text).subscribe(response => {
  //     const nodes = response.nodes.map(node => ({data: {id: node.name}}));
  //     const edges = response.edges.map(edge => ({data: {id: edge.name, source: edge.domain, target: edge.range}}));
  //
  //     this.cyst = cytoscape({
  //       container: document.getElementById('cy'),
  //       style: [
  //         {
  //           selector: 'node::hover',
  //           style: {
  //             'background-color': 'cornflowerblue',
  //           },
  //         },
  //         {
  //           selector: 'node',
  //           style: {
  //             'font-weight': 'bold',
  //             'background-color': 'lightblue',
  //             'label': 'data(id)',
  //             'text-halign': 'center',
  //             'text-valign': 'center',
  //             'font-size': '12px',
  //             'color': 'black',
  //             'width': function (ele: { data: (arg0: string) => { (): any; new(): any; length: number; }; }) {
  //               return (ele.data('id').length * 7) + 'px';
  //             },
  //             'height': function (ele) {
  //               return (ele.data('id').length * 7) + 'px';
  //             },
  //             'shape': 'ellipse',
  //           }
  //         },
  //         {
  //           selector: 'edge',
  //           style: {
  //             'font-weight': 'bold',
  //             'font-size': '15px',
  //             'curve-style': 'bezier',
  //             'target-arrow-shape': 'triangle',
  //             'label': 'data(id)',
  //             'line-color': 'lightblue',
  //             'target-arrow-color': '#ccc',
  //             'target-arrow-fill': 'filled'
  //           }
  //         }
  //       ],
  //       elements: [
  //         ...nodes,
  //         ...edges
  //       ],
  //       layout: {
  //         name: 'cose'
  //       }
  //     });
  //   });
  //
  // }

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

  class() {

  }

  op() {

  }

  dp() {

  }

  individual() {

  }
}
