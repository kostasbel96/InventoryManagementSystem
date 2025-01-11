import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListGroupMenuComponent } from './list-group-menu.component';

describe('ListGroupMenuComponent', () => {
  let component: ListGroupMenuComponent;
  let fixture: ComponentFixture<ListGroupMenuComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ListGroupMenuComponent]
    });
    fixture = TestBed.createComponent(ListGroupMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
