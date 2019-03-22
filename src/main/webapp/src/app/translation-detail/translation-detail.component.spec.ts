import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslationDetailComponent } from './translation-detail.component';

describe('TranslationDetailComponent', () => {
  let component: TranslationDetailComponent;
  let fixture: ComponentFixture<TranslationDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TranslationDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TranslationDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
