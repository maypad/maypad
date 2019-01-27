import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProjectService } from './project.service';
import { TestBed, fakeAsync } from '@angular/core/testing';
import { environment } from '../environments/environment';
import { BuildStatus } from './model/buildStatus';
import { UserServiceAccount } from './model/serviceAccount';
import * as get_projects_id_response from '../../sample-requests/get.projects.id.response.json';
import * as get_projects_id_branches_response from '../../sample-requests/get.projects.id.branches.response.json';
import * as put_projects_id_response from '../../sample-requests/put.projects.id.response.json';
import * as put_projects_id_request from '../../sample-requests/put.projects.id.request.json';

describe('Service: ProjectService', () => {
  let service: ProjectService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProjectService]
    });
    service = TestBed.get(ProjectService);
    httpTestingController = TestBed.get(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('loads a specific project',
    fakeAsync(() => {
      service.loadProject(2).subscribe((data) => {
        expect(data.id).toEqual(2);
        expect(data.name).toEqual('One Test Project');
        expect(data.status).toEqual(BuildStatus.SUCCESS);
        expect(data.repositoryUrl).toEqual('https://git.maypad.de/one-test-project.git');
        expect((<UserServiceAccount>data.serviceAccount).username).toEqual('maypad-git');
      });

      const req = httpTestingController.expectOne(`${environment.baseUrl}projects/2`);
      expect(req.request.method).toEqual('GET');
      req.flush(get_projects_id_response['default']);
    })
  );

  it('loads all branches for a specific project',
    fakeAsync(() => {
      service.loadBranches(2).subscribe((data) => {
        expect(data.length).toEqual(2);

        expect(data[0].name).toEqual('master');
        expect(data[0].readme).toEqual('# MAYPAD\nDocumentation: [https://github.com/juliantodt/'
          + 'maypad-docs](https://github.com/juliantodt/maypad-docs)');
        expect(data[0].status).toEqual(BuildStatus.SUCCESS);
        expect(data[0].lastCommit.author).toEqual('Developer One <developer.one@maypad.de>');
        expect(data[0].lastCommit.message).toEqual('Fix various bugs');
        expect(data[0].lastCommit.identifier).toEqual('e37ab2d1f1eddc11b1b6531372569793bd110b83');
        expect(data[0].lastCommit.timestamp).toEqual('Wed Jan 2 14:37:30 2019 +0100');
        expect(data[0].members).toEqual(['Developer One', 'Developer Two']);
        expect(data[0].mails).toEqual(['developer.one@maypad.de', 'developer.two@maypad.de']);
        expect(data[0].buildSuccessUrl).toEqual('https://maypad.de/projects/1/branches/master/'
          + 'builds/success?token=djhbvjskbfavhbkdfbvlfbva');
        expect(data[0].buildFailUrl).toEqual('https://maypad.de/projects/1/branches/master/builds/fail?token=jhdsbfajdbfhjbhvjdafb');
        expect(data[0].dependencies).toEqual(['3:master', '12:dev', '24:master']);
        expect(data[0].deployment).toEqual('webhook: https://ship.maypad.de/hook?token=vjfsdbjhkvlfavhkl');
        expect(data[0].buildWebhook).toEqual('https://git.maypad.de/pipeline-hook?token=kjadbhjasebjkdsa');

        expect(data[1].name).toEqual('dev');
        expect(data[1].readme).toEqual('# MAYPAD\nDocumentation: [https://github.com/juliantodt/'
          + 'maypad-docs](https://github.com/juliantodt/maypad-docs)');
        expect(data[1].status).toEqual(BuildStatus.RUNNING);
        expect(data[1].lastCommit.author).toEqual('Developer One <developer.one@maypad.de>');
        expect(data[1].lastCommit.message).toEqual('Fix typo');
        expect(data[1].lastCommit.identifier).toEqual('e37ab2d1f1eddc11b1b6531372569793bd110b83');
        expect(data[1].lastCommit.timestamp).toEqual('Wed Jan 2 14:39:32 2019 +0100');
        expect(data[1].members).toEqual(['Developer One', 'Developer Two']);
        expect(data[1].mails).toEqual(['developer.one@maypad.de', 'developer.two@maypad.de']);
        expect(data[1].buildSuccessUrl).toEqual('https://maypad.de/projects/1/branches/dev/'
          + 'builds/success?token=adjvnfjkvadnfjkvnkjsfd');
        expect(data[1].buildFailUrl).toEqual('https://maypad.de/projects/1/branches/dev/builds/fail?token=sajkvdbfdjkvnfjksvnjkfdv');
        expect(data[1].dependencies).toEqual(['3:dev', '12:testing', '24:dev']);
        expect(data[1].deployment).toEqual('webhook: https://ship.maypad.de/hook?token=jabjdksvnkf');
        expect(data[1].buildWebhook).toEqual('https://git.maypad.de/pipeline-hook?token=sadjkcndfjkvfkv');
      });

      const req = httpTestingController.expectOne(`${environment.baseUrl}projects/2/branches`);
      expect(req.request.method).toEqual('GET');
      req.flush(get_projects_id_branches_response['default']);
    })
  );

  it('updates the servicesaccount of a project',
    fakeAsync(() => {
      const account = new UserServiceAccount();
      account.username = 'maypad-git';
      account.password = 'ultrageheim';
      service.updateServiceAccount(2, account).subscribe((data) => {
        expect(data.id).toEqual(2);
        expect(data.name).toEqual('One Test Project');
        expect(data.status).toEqual(BuildStatus.SUCCESS);
        expect(data.repositoryUrl).toEqual('https://git.maypad.de/one-test-project.git');
        expect((<UserServiceAccount>data.serviceAccount).username).toEqual('maypad-git');
      });

      const req = httpTestingController.expectOne(`${environment.baseUrl}projects/2`);
      expect(req.request.method).toEqual('PUT');
      expect(JSON.parse(JSON.stringify(req.request.body))).toEqual(put_projects_id_request['default']);
      req.flush(put_projects_id_response['default']);
    })
  );

  it('refresh a branch',
    fakeAsync(() => {
      service.refreshProject(2).subscribe();

      const req = httpTestingController.expectOne(`${environment.baseUrl}projects/2/refresh`);
      expect(req.request.method).toEqual('POST');
      req.flush(null, { status: 202, statusText: 'Accepted' });
    })
  );
});
