import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchListItemComponent } from './branch-list-item.component';
import { RouterTestingModule } from '@angular/router/testing';
import * as response from 'sample-requests/get.projects.id.branches.response.json';
import { Branch } from 'src/app/model/branch';

describe('BranchListItemComponent', () => {
  let component: BranchListItemComponent;
  let fixture: ComponentFixture<BranchListItemComponent>;
  const branch: Branch = response['default'][0];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [BranchListItemComponent],
      imports: [RouterTestingModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BranchListItemComponent);
    component = fixture.componentInstance;
    component.branch = branch;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show info', () => {
    const nativeElement: HTMLElement = fixture.nativeElement;
    expect(nativeElement.querySelector('h5').textContent).toBe(' ' + branch.name + ' ');
  });
});
