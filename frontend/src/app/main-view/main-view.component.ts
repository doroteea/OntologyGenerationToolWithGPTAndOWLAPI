import {Component, ElementRef, HostListener, Input, OnInit, ViewChild} from '@angular/core';

import * as cytoscape from 'cytoscape';

import {GeneratorService} from '../service/generatorService';
import {HttpClient} from "@angular/common/http";
import { saveAs } from 'file-saver';
import { AfterViewChecked } from '@angular/core';
import {BaseMetrics} from "../model/BaseMetrics";
import {ClassAxiomsMetrics} from "../model/ClassAxiomsMetrics";
import {ObjectPropertyAxiomsMetrics} from "../model/ObjectPropertyAxiomsMetrics";
import {DataPropertyAxiomsMetrics} from "../model/DataPropertyAxiomsMetrics";
import {IndividualAxiomsMetrics} from "../model/IndividualAxiomsMetrics";

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
  constructor(private service: GeneratorService,private httpClient: HttpClient) {}

  // ngAfterViewChecked(): void {
  //   // Check if 'Generate' view is currently active
  //   if (this.num === '1') {
  //     // If the savedGraphState exists, restore the graph state
  //     if (this.savedGraphState) {
  //       this.graphState = JSON.parse(JSON.stringify(this.savedGraphState));
  //     }
  //
  //     // Initialize or re-initialize the cy object
  //     this.cy = cytoscape({
  //       container: document.getElementById('cy'),
  //       elements: {
  //         nodes: this.graphState.nodes,
  //         edges: this.graphState.edges,
  //       },
  //     });
  //
  //     // Update graph with empty updates to refresh the graph without making any changes
  //     // Also, make sure that updateGraph method won't mutate this.graphState if updates are empty
  //     this.updateGraph([]);
  //   }
  // }

//triplets
  ontology_list! : any[];
  apiKey = "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";
  SELECTED_PROMPT = "STATELESS";

  graphState = { nodes: [], edges: [] };
  prompt = '';

  buttonClass = '';
  buttonText = '';

  ngOnInit(): void {
    this.buttonClass = 'button';
    this.buttonText = 'RDF Schema';
    this.initializeCy();
    this.convert();
  }

  initializeCy() {
    this.cy = cytoscape({
      container: document.getElementById('cy'),
      elements: {
        nodes: this.graphState.nodes,
        edges: this.graphState.edges,
      },
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
  this.clearGraph();
  }

  updateGraph(updates: any[]) {
    let current_graph = JSON.parse(JSON.stringify(this.graphState));

    if (updates.length === 0) {
      return;
    }

    if (typeof updates[0] === "string") {
      updates = [updates];
    }

    console.log("updates: ");
    console.log(updates)
    updates.forEach(update => {
      if (update.length === 3) {
        const [entity1, relation, entity2] = update;
        // if (relation.includes('rdf')) {
        //   return;
        // }
        let node1 = current_graph.nodes.find((node: { id: any; }) => node.id === entity1);
        let node2 = current_graph.nodes.find((node: { id: any; }) => node.id === entity2);

        if (node1 === undefined) {
          current_graph.nodes.push({ id: entity1, label: entity1, color: "#ffffff" });
        }

        if (node2 === undefined) {
          current_graph.nodes.push({ id: entity2, label: entity2, color: "#ffffff" });
        }

        let edge = current_graph.edges.find((edge: { from: any; to: any; }) => edge.from === entity1 && edge.to === entity2);
        if (edge !== undefined) {
          edge.label = relation;
          return;
        }

        current_graph.edges.push({ from: entity1, to: entity2, label: relation });

      } else if (update.length === 2 && update[1].startsWith("#")) {
        const [entity, color] = update;

        let node = current_graph.nodes.find((node: { id: any; }) => node.id === entity);

        if (node === undefined) {
          current_graph.nodes.push({ id: entity, label: entity, color: color });
          return;
        }

        node.color = color;

      } else if (update.length === 2 && update[0] === "DELETE") {
        const [_, index] = update;

        let node = current_graph.nodes.find((node: { id: any; }) => node.id === index);

        if (node === undefined) {
          return;
        }

        current_graph.nodes = current_graph.nodes.filter((node: { id: any; }) => node.id !== index);
        current_graph.edges = current_graph.edges.filter((edge: { from: any; to: any; }) => edge.from !== index && edge.to !== index);
      }
    });

    this.graphState = current_graph;

    console.log("before cysto: ");
    console.log(this.graphState);
    // @ts-ignore
    const nodes = this.graphState.nodes.map(node => ({ data: { id: node.id, label: node.label, color: node.color } }));
    // @ts-ignore
    const edges = this.graphState.edges.map(edge => ({ data: { id: edge.id, source: edge.from, target: edge.to, label: edge.label } }));

    this.cy = cytoscape({
      container: document.getElementById('cy'),
      elements: [
        ...nodes,
        ...edges
      ],
      style: [
        {
          selector: 'node',
          style: {
            'label': 'data(label)',
            'background-color': '#6495ed',
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
    document.body.style.cursor = "auto";
  }

  clearGraph() {
    console.log(this.graphState);
    // @ts-ignore
    this.cy = cytoscape({
      container: document.getElementById('cy'),
      elements: [
        // @ts-ignore
        ...(this.graphState.nodes.map(node => ({ data: { id: node.id, label: node.label, color: node.color } }))),
        // @ts-ignore
        ...(this.graphState.edges.map(edge => ({ data: { id: edge.id, source: edge.from, target: edge.to, label: edge.label } })))
      ],
      style: [
        {
          selector: 'node',
          style: {
            'label': 'data(label)',
            'background-color': '#6495ed',
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

  validateOntology() {
    if (this.onto) {
      this.service.validateOntology(this.onto).subscribe((data: any) => {
        alert(data);
      });
    }
  }


  async queryStatelessPrompt(prompt: string, apiKey: string) {
    try {
      const response = await this.httpClient.get('assets/prompt/stateless.prompt', { responseType: 'text' }).toPromise();
      // @ts-ignore
      const promptText = response.replace("$prompt", prompt);
      console.log(promptText);

      this.service.generateOntology(apiKey, promptText).subscribe((data: any[]) => {

        const triples = data.toString().replace("],\n]", "]]");
        console.log("data :" + data);
        this.ontology_list = data;
        if (data.length > 0) {
          const updates = JSON.parse(data[0]);
          console.log(updates);
          this.updateGraph(updates);
        } else {
          console.error('Invalid data format');
        }
      });

    } catch (error) {
      console.log(error);
      alert(error);
    }
  }

  async queryStatefulPrompt(prompt: string, apiKey: string) {
    try {
      const response = await this.httpClient.get('assets/prompt/stateful.prompt', { responseType: 'text' }).toPromise();
      // @ts-ignore
      const promptText = response.replace("$prompt", prompt);
      console.log(promptText);

      this.service.generateOntology(apiKey, promptText).subscribe((data: any[]) => {
        this.ontology_list = data;
        if (data.length > 0) {
          const choices = JSON.parse(data[0]);
          const text = choices[0];
          console.log(text);

          const updates = JSON.parse(`[${text.map((value: any) => `"${value}"`).join(',')}]`);

          //this.list.push(...updates);
          //console.log(updates);

          this.updateGraph(updates);
        } else {
          console.error('Invalid data format');
        }
      });
    } catch (error) {
      console.log(error);
      alert(error);
    }
  }

  async queryPrompt(prompt: string, apiKey: string) {
    if (this.SELECTED_PROMPT === "STATELESS") {
      await this.queryStatelessPrompt(prompt, apiKey);
    } else if (this.SELECTED_PROMPT === "STATEFUL") {
      await this.queryStatefulPrompt(prompt, apiKey);
    } else {
      alert("Please select a prompt");
    }
  }

  // clearState() {
  //   this.graphState = {
  //     nodes: [],
  //     edges: []
  //   };
  // }

  generateFunction() {
    document.body.style.cursor = 'wait';

    //(document.getElementsByClassName("generateButton")[0] as HTMLButtonElement).disabled = true;
    const apiKeyInput = document.getElementById("apikey") as HTMLInputElement;
    const apiKey = apiKeyInput.value;
console.log(apiKey)
    const prompt: string = (document.getElementsByClassName("abstract")[0] as HTMLInputElement).value;

    this.queryPrompt(prompt, apiKey);
  }

  changeGraph() {
    this.buttonClass = this.buttonClass === 'button' ? 'button_secundar' : 'button';
    this.buttonText = this.buttonText === 'RDF Schema' ? 'Knowledge Schema' : 'RDF Schema';
  }

  tripleToOntology() {
    const triplets = String(this.ontology_list);
    console.log(triplets);
    this.service.tripletsToOntology(triplets).subscribe((data: any) => {
            console.log(data);
            console.log(data[0]);
            this.onto = data[0];
            this.hideImage();
          });
    this.num = '2';

  }

  performMetricsRequest() {
    if (this.onto) {
      console.log(this.onto);
      this.service.getBaseMetrics(this.onto).subscribe(response => {
        this.baseMetrics = response;
      });
      this.service.getClassMetrics(this.onto).subscribe(response => {
        this.classMetrics = response;
      });
      this.service.getDataMetrics(this.onto).subscribe(response => {
        this.dataMetrics = response;
      });
      this.service.getIndividualMetrics(this.onto).subscribe(response => {
        this.individualMetrics = response;
      });
      this.service.getObjectMetrics(this.onto).subscribe(response => {
        this.objectMetrics = response;
      });
    }
  }

  performValidation() {
    this.service.validateOntology(this.onto).subscribe((data: any) => {
      this.validationReport = this.formatValidationReport(data);
    });
  }

  formatValidationReport(report: string): string {
    const statements = report.split("On statement: ");
    let formattedReport = "";

    for (const statement of statements.slice(1)) {
      const [subject, predicate, ignored1, objectValue, ignored2] = statement.split(" ");

      const formattedStatement = `${subject} ${predicate}: ${objectValue}\n`;
      formattedReport += formattedStatement;
    }

    return formattedReport.trim();
  }

  convert(){
    const dropdown = document.getElementById("dropdown") as HTMLSelectElement;
    dropdown.addEventListener("change", (event) => {
      // @ts-ignore
      this.selectedOntologySyntax = event.target.value;
      console.log(this.selectedOntologySyntax);
      let syntax!: string;
      switch (this.selectedOntologySyntax) {
        case 'OWL':
          syntax = 'owl';
          break;
        case 'RDF':
          syntax = 'rdf';
          break;
        case 'TURTLE':
          syntax = 'ttl';
          break;
        case 'Manchester Syntax':
          syntax = 'ms';
          break;
        case 'Functional Syntax':
          syntax = 'fs';
          break;
        case 'KRSS2':
          syntax = 'krss2';
          break;
        default:
          syntax = 'txt';
          break;
      }
      console.log(syntax);
      this.service.convertOntology(syntax, this.onto).subscribe((data: any) => {
        console.log(data);
        console.log(data[0]);
        this.converted = data[0];
      });
    });
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
}
