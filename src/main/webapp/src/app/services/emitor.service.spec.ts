import { TestBed, inject } from '@angular/core/testing';

import { EmitorService } from './emitor.service';

describe('EmitorService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [EmitorService]
    });
  });

  it('should be created', inject([EmitorService], (service: EmitorService) => {
    expect(service).toBeTruthy();
  }));
});
