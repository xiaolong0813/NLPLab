import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';

import { ModalModule } from "ngx-bootstrap";
import { NgSelectModule } from "@ng-select/ng-select";
import { NgxDatatableModule } from "@swimlane/ngx-datatable";

import { AppComponent } from './app.component';
import { IndexComponent } from "./index/index.component";
import { DeviationsComponent } from "./deviations/deviations.component";
import { DeviationDetailComponent } from "./deviation-detail/deviation-detail.component";
import { ModalContentComponent } from "./modal-content/modal-content.component";
import { ModalRfqComponent } from "./modal-rfq/modal-rfq.component";
import { RfqComponent } from "./rfq/rfq.component";
import { SettingsComponent } from "./settings/settings.component";
import { HelpComponent } from "./help/help.component";
import { MessagesComponent } from './messages/messages.component';
import { TranslationComponent } from './translation/translation.component';
import { TranslationDetailComponent } from './translation-detail/translation-detail.component';

@NgModule({
  declarations: [
    AppComponent,
    IndexComponent,
    DeviationsComponent,
    DeviationDetailComponent,
    ModalContentComponent,
    ModalRfqComponent,
    RfqComponent,
    SettingsComponent,
    HelpComponent,
    MessagesComponent,
    TranslationComponent,
    TranslationDetailComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    AppRoutingModule,
    HttpClientModule,

    ModalModule.forRoot(),
    NgxDatatableModule,
    NgSelectModule
  ],
  entryComponents: [
    ModalContentComponent,
    ModalRfqComponent
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
