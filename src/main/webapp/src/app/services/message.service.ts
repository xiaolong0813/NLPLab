import { Injectable } from '@angular/core';
import {interval, Observable, Subject, Subscription} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  alert_new = false;
  alert_type = "success";
  alert_message = "This is a message.";

  new_alert(code: number, message: string) {
    switch (code) {
      case 200: {
        this.alert_type = "success";
        break;
      }
      case -1: {
        this.alert_type = "danger";
        break;
      }
      default: {
        break;
      }
    }
    this.alert_message = message;
    this.alert_new = true;

    setTimeout(()=> {
      this.alert_new = false;
    }, 5000)
  }

  close_alert() {
    this.alert_new = false;
  }



}
