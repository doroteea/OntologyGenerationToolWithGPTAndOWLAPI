import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { GeneratorService } from '../service/generatorService';

@Component({
  selector: 'app-main-view',
  templateUrl: './main-view.component.html',
  styleUrls: ['./main-view.component.css'],
})
export class MainViewComponent implements OnInit {
  @Input() onto!: string;
  selectedFile!: File;
  isFileSelected = false;
  fileContent!: string;

  constructor(private service: GeneratorService) {}

  ngOnInit(): void {
    this.isFileSelected = false;
    // this.hideImage();
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
    if (this.isFileSelected) {
      value += this.fileContent.toString();
    }
    console.log(value);
    const deactivateBtn = document.getElementById(
      'deactivateBtn'
    ) as HTMLButtonElement;
    const myDiv = document.getElementById('myDiv') as HTMLDivElement;
    myDiv.style.opacity = '0.5'; // set the opacity to make it look deactivated
    myDiv.style.pointerEvents = 'none'; // disable pointer events to prevent user interaction
    this.showImage();

    this.service.generateOntology(value).subscribe((data: any) => {
      console.log(data);
      console.log(data[0]);
      this.onto = data[0];
      myDiv.style.opacity = '1'; // reset the opacity
      myDiv.style.pointerEvents = 'auto'; // enable pointer events
      this.hideImage();
    });
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


}
