import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import {AppRoutingModule, RoutingComponents} from './app-routing.module';
import { AppComponent } from './app.component';
import { MainViewComponent } from './main-view/main-view.component';
import {HttpClientModule} from "@angular/common/http";
import {RouterModule} from "@angular/router";
import {CommonModule} from "@angular/common";
import { HighlightModule, HIGHLIGHT_OPTIONS } from 'ngx-highlightjs';
import hljs from 'highlight.js/lib/core';
import xml from 'highlight.js/lib/languages/xml';
import owl from 'highlight.js/lib/languages/xml';

hljs.registerLanguage('xml', xml);
hljs.registerLanguage('owl', owl);

@NgModule({
  declarations: [
    AppComponent,
    MainViewComponent,
    RoutingComponents
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    CommonModule,
    RouterModule,
    BrowserModule,
    HighlightModule,
  ],
  providers: [{
    provide: HIGHLIGHT_OPTIONS,
    useValue: {
      languages: {
        'owl/xml': () => import('highlight.js/lib/languages/xml'),
        'rdf/xml': () => import('highlight.js/lib/languages/xml'),
      }
    }
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
