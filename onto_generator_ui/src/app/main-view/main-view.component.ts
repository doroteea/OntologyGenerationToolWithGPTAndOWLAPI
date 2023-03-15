import {Component, Input, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {GeneratorService} from "../service/generatorService";

@Component({
  selector: 'app-main-view',
  templateUrl: './main-view.component.html',
  styleUrls: ['./main-view.component.css']
})
export class MainViewComponent implements OnInit {

  @Input() onto!: string;

  constructor(private service: GeneratorService) { }

  ngOnInit(): void {
  }

  generateFunction(value: string) {
    this.service.generateOntology(value).subscribe((data:any) => {
      console.log(data);
      console.log(data[0])
      this.onto = data[0];
    })
  }
}
