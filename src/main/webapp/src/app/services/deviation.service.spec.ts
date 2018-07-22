import { TestBed, inject } from '@angular/core/testing';

import { DeviationService } from './deviation.service';

describe('DeviationService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DeviationService]
    });
  });

  it('should be created', inject([DeviationService], (service: DeviationService) => {
    expect(service).toBeTruthy();
  }));
});
