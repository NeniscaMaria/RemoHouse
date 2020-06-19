import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {HomeComponent} from "./components/home/home.component";
import {RegisterComponent} from "./components/register/register.component";
import {AboutusComponent} from "./components/aboutus/aboutus.component";
import {GetstartedComponent} from "./components/getstarted/getstarted.component";


const routes: Routes = [
  {path: 'home', component: HomeComponent},
  {path: '', component: HomeComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'about', component: AboutusComponent},
  {path: 'getstarted', component: GetstartedComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }