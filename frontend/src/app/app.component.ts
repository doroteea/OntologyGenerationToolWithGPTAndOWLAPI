import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import cytoscape, {Core} from 'cytoscape';
import {MainViewService} from "./service/mainViewService";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  ngOnInit(): void {
  }
  // cy: Core | undefined;
  // list = [];
  // apiKey = "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";
  // SELECTED_PROMPT = "STATELESS";
  // graphState = { nodes: [], edges: [] };
  //
  // constructor(private service: MainViewService,private httpClient: HttpClient) { }
  //
  // ngOnInit() {
  //   this.cy = cytoscape({
  //     container: document.getElementById('cy'),
  //     elements: {
  //       nodes: this.graphState.nodes,
  //       edges: this.graphState.edges,
  //     },
  //   });
  // }
  //
  // updateGraph(updates: any[]) {
  //   let current_graph = JSON.parse(JSON.stringify(this.graphState));
  //   console.log("current status: ");
  //   console.log(current_graph);
  //   if (updates.length === 0) {
  //     return;
  //   }
  //
  //   if (typeof updates[0] === "string") {
  //     updates = [updates];
  //   }
  //   console.log("updates: ");
  //   console.log(updates)
  //   updates.forEach(update => {
  //     if (update.length === 3) {
  //       const [entity1, relation, entity2] = update;
  //
  //       let node1 = current_graph.nodes.find((node: { id: any; }) => node.id === entity1);
  //       let node2 = current_graph.nodes.find((node: { id: any; }) => node.id === entity2);
  //
  //       if (node1 === undefined) {
  //         current_graph.nodes.push({ id: entity1, label: entity1, color: "#ffffff" });
  //       }
  //
  //       if (node2 === undefined) {
  //         current_graph.nodes.push({ id: entity2, label: entity2, color: "#ffffff" });
  //       }
  //
  //       let edge = current_graph.edges.find((edge: { from: any; to: any; }) => edge.from === entity1 && edge.to === entity2);
  //       if (edge !== undefined) {
  //         edge.label = relation;
  //         return;
  //       }
  //
  //       current_graph.edges.push({ from: entity1, to: entity2, label: relation });
  //
  //     } else if (update.length === 2 && update[1].startsWith("#")) {
  //       const [entity, color] = update;
  //
  //       let node = current_graph.nodes.find((node: { id: any; }) => node.id === entity);
  //
  //       if (node === undefined) {
  //         current_graph.nodes.push({ id: entity, label: entity, color: color });
  //         return;
  //       }
  //
  //       node.color = color;
  //
  //     } else if (update.length === 2 && update[0] === "DELETE") {
  //       const [_, index] = update;
  //
  //       let node = current_graph.nodes.find((node: { id: any; }) => node.id === index);
  //
  //       if (node === undefined) {
  //         return;
  //       }
  //
  //       current_graph.nodes = current_graph.nodes.filter((node: { id: any; }) => node.id !== index);
  //       current_graph.edges = current_graph.edges.filter((edge: { from: any; to: any; }) => edge.from !== index && edge.to !== index);
  //     }
  //   });
  //
  //   this.graphState = current_graph;
  //
  //   console.log("before cysto: ");
  //   console.log(this.graphState);
  //   // @ts-ignore
  //   const nodes = this.graphState.nodes.map(node => ({ data: { id: node.id, label: node.label, color: node.color } }));
  //   // @ts-ignore
  //   const edges = this.graphState.edges.map(edge => ({ data: { id: edge.id, source: edge.from, target: edge.to, label: edge.label } }));
  //
  //   this.cy = cytoscape({
  //     container: document.getElementById('cy'),
  //     elements: [
  //       ...nodes,
  //       ...edges
  //     ],
  //     style: [
  //       {
  //         selector: 'node',
  //         style: {
  //           'label': 'data(label)',
  //           'background-color': 'data(color)',
  //           'border-color': '#000',
  //           'border-width': '1px',
  //           'width': '50px',
  //           'height': '50px'
  //         }
  //       },
  //       {
  //         selector: 'edge',
  //         style: {
  //           'label': 'data(label)',
  //           'curve-style': 'bezier',
  //           'target-arrow-shape': 'triangle',
  //           'target-arrow-color': '#000',
  //           'line-color': '#000',
  //           'width': '1px'
  //         }
  //       }
  //     ]
  //   });
  //
  // }
  //
  //
  // async queryStatelessPrompt(prompt: string, apiKey: string) {
  //   try {
  //     const response = await this.httpClient.get('assets/prompt/stateless.prompt', { responseType: 'text' }).toPromise();
  //     // @ts-ignore
  //     const promptText = response.replace("$prompt", prompt);
  //     console.log(promptText);
  //
  //     this.service.generateOntology("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", promptText).subscribe((data: any[]) => {
  //       console.log("data :" + data);
  //       if (data.length > 0) {
  //         const updates = JSON.parse(data[0]);
  //         console.log(updates);
  //
  //         this.updateGraph(updates);
  //       } else {
  //         console.error('Invalid data format');
  //       }
  //     });
  //
  //   } catch (error) {
  //     console.log(error);
  //     alert(error);
  //   }
  // }
  //
  //
  // async queryStatefulPrompt(prompt: string, apiKey: string) {
  //   try {
  //     const response = await this.httpClient.get('assets/prompt/stateful.prompt', { responseType: 'text' }).toPromise();
  //     // @ts-ignore
  //     const promptText = response.replace("$prompt", prompt);
  //     console.log(promptText);
  //
  //     this.service.generateOntology("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", promptText).subscribe((data: any[]) => {
  //
  //       if (data.length > 0) {
  //         const choices = JSON.parse(data[0]);
  //         const text = choices[0];
  //         console.log(text);
  //
  //         const updates = JSON.parse(`[${text.map((value: any) => `"${value}"`).join(',')}]`);
  //
  //         //this.list.push(...updates);
  //         //console.log(updates);
  //
  //         this.updateGraph(updates);
  //       } else {
  //         console.error('Invalid data format');
  //       }
  //     });
  //   } catch (error) {
  //     console.log(error);
  //     alert(error);
  //   }
  // }
  //
  // async queryPrompt(prompt: string, apiKey: string) {
  //   if (this.SELECTED_PROMPT === "STATELESS") {
  //     await this.queryStatelessPrompt(prompt, apiKey);
  //   } else if (this.SELECTED_PROMPT === "STATEFUL") {
  //     await this.queryStatefulPrompt(prompt, apiKey);
  //   } else {
  //     alert("Please select a prompt");
  //   }
  // }
  //
  // clearState() {
  //   this.graphState = {
  //     nodes: [],
  //     edges: []
  //   };
  // }
  //
  //
  // createGraph() {
  //   //document.body.style.cursor = 'wait';
  //
  //   //(document.getElementsByClassName("generateButton")[0] as HTMLButtonElement).disabled = true;
  //   const prompt: string = (document.getElementsByClassName("searchBar")[0] as HTMLInputElement).value;
  //   const apiKey: string = (document.getElementsByClassName("apiKeyTextField")[0] as HTMLInputElement).value;
  //
  //   this.queryPrompt(prompt, apiKey);
  // }
  //
  // createList() {
  //   console.log(JSON.stringify(this.list));
  //   const blob = new Blob([JSON.stringify(this.list)], { type: 'text/plain' });
  //
  //   // Create a temporary URL for the Blob
  //   const url = URL.createObjectURL(blob);
  //
  //   // Create a link element
  //   const link = document.createElement('a');
  //   link.href = url;
  //   link.download = 'list.txt';
  //
  //   // Programmatically click the link to trigger the download
  //   link.click();
  //
  //   // Clean up by revoking the URL
  //   URL.revokeObjectURL(url);
  // }

}
