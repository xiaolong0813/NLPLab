import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-help',
  templateUrl: './help.component.html',
  styleUrls: [ './help.component.scss' ]
})
export class HelpComponent {
  title = "Hello, World";
  message = "This is help page.";
}
