import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { IndexComponent }   from './index/index.component';
import { DeviationsComponent } from "./deviations/deviations.component";
import { RfqComponent } from "./rfq/rfq.component";
import { SettingsComponent } from "./settings/settings.component";
import { HelpComponent } from "./help/help.component";

const routes: Routes = [
  { path: '', redirectTo: '/index', pathMatch: 'full' },
  { path: 'index', component: IndexComponent },
  { path: 'devlist', redirectTo:'/devlist/0', pathMatch: 'full' },
  { path: 'devlist/:type', component: DeviationsComponent},
  { path: 'rfq', component: RfqComponent },
  { path: 'settings', component: SettingsComponent },
  { path: 'help', component: HelpComponent }
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
