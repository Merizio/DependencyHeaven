import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Dependencies } from './dependencies';

describe('Dependencies', () => {
  let component: Dependencies;
  let fixture: ComponentFixture<Dependencies>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Dependencies],
    }).compileComponents();

    fixture = TestBed.createComponent(Dependencies);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
