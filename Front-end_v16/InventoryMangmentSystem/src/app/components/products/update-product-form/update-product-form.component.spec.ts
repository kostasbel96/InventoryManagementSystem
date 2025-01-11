import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateProductFormComponent } from './update-product-form.component';

describe('UpdateProductFormComponent', () => {
  let component: UpdateProductFormComponent;
  let fixture: ComponentFixture<UpdateProductFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UpdateProductFormComponent]
    });
    fixture = TestBed.createComponent(UpdateProductFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
