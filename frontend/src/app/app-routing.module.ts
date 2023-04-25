import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MainViewComponent} from "./main-view/main-view.component";

const routes: Routes = [
  {
    path: '',
    redirectTo: 'main-view',
    pathMatch: 'full',
  },
  {
    path:'main-view',
    component: MainViewComponent,
  }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

export const RoutingComponents = [MainViewComponent]
