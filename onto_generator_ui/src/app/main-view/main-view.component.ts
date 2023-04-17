import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import * as cytoscape from 'cytoscape';
import * as owljs from 'owljs';

import { GeneratorService } from '../service/generatorService';

@Component({
  selector: 'app-main-view',
  templateUrl: './main-view.component.html',
  styleUrls: ['./main-view.component.css'],
})
export class MainViewComponent implements OnInit {
  @Input() onto!: string;
  selectedFile: File | null = null;
  isFileSelected = false;
  fileContent: string = '';
  base_ontology!: string;

  constructor(private service: GeneratorService) {}

  ngOnInit(): void {
    this.isFileSelected = false;
    this.getGraph();
  }

  getGraph() {
    console.log("here")
    // Load the .owl file using owljs
    // const owlFile = './path/to/file.owl';
    // const parser = new owljs.Parser();
    // const kb = parser.parse(owlFile);

    // Create a Cytoscape.js graph
    const cy = cytoscape({
      container: document.getElementById('cy'),
      elements: [
        { data: { id: 'a' } },
        { data: { id: 'b' } },
        { data: { id: 'ab', source: 'a', target: 'b' } }
      ],
      style: [
        {
          selector: 'node',
          style: {
            'background-color': '#666',
            'label': 'data(id)'
          }
        },
        {
          selector: 'edge',
          style: {
            'width': 3,
            'line-color': '#ccc',
            'target-arrow-color': '#ccc',
            'target-arrow-shape': 'triangle'
          }
        }
      ]
    });

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


}
