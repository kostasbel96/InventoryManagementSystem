import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateSupplierFormComponent } from './update-supplier-form.component';

describe('UpdateSupplierFormComponent', () => {
  let component: UpdateSupplierFormComponent;
  let fixture: ComponentFixture<UpdateSupplierFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UpdateSupplierFormComponent]
    });
    fixture = TestBed.createComponent(UpdateSupplierFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
