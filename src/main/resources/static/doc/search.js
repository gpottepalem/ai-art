let api = [];
api.push({
    alias: 'api',
    order: '1',
    desc: 'OllamaController',
    link: 'ollamacontroller',
    list: []
})
api[0].list.push({
    order: '1',
    methodId: 'b14f363f9b8d810e2817436330a1f762',
    desc: 'generate',
});
api[0].list.push({
    order: '2',
    methodId: 'ab18238bb9e4159f15e5b45c64f70de7',
    desc: 'stream',
});
api[0].list.push({
    order: '3',
    methodId: '6e3cd9e29915983b8a716380071bd39f',
    desc: 'streamJson',
});
api[0].list.push({
    order: '4',
    methodId: '56634af92ee2f686f328688ffb51d414',
    desc: 'inquire',
});
api.push({
    alias: 'ArtMasterController',
    order: '2',
    desc: 'Art Master Controller - demonstrates chatting about learning Art with AI',
    link: 'art_master_controller_-_demonstrates_chatting_about_learning_art_with_ai',
    list: []
})
api[1].list.push({
    order: '1',
    methodId: '260cdedf2bf3b1046b8715a475b014d6',
    desc: '/master',
});
api[1].list.push({
    order: '2',
    methodId: '94db77a81eb3a67d4f6bbeb0e114d85b',
    desc: '/master/raw',
});
api[1].list.push({
    order: '3',
    methodId: 'cbd3d8856baac3d352260f5f46400fd8',
    desc: '/master/raw/client',
});
api[1].list.push({
    order: '4',
    methodId: 'da9ce4a9d53525a6ed6a575bc5f0fda6',
    desc: '/master/raw/stream',
});
api[1].list.push({
    order: '5',
    methodId: 'c6789631eff9777f0f21f4250bdb9e40',
    desc: '/master/stream',
});
api[1].list.push({
    order: '6',
    methodId: '8cef69d396e75b3c5ac6781dd1517f25',
    desc: '/master/words/stream',
});
api[1].list.push({
    order: '7',
    methodId: 'd81a2b7a3ee67d6cd3396245c83e512e',
    desc: '/master/paintings',
});
api[1].list.push({
    order: '8',
    methodId: 'fc909c4d9d8fbd8a3d41c1c829c899fd',
    desc: '/master/paintings/stream',
});
document.onkeydown = keyDownSearch;
function keyDownSearch(e) {
    const theEvent = e;
    const code = theEvent.keyCode || theEvent.which || theEvent.charCode;
    if (code === 13) {
        const search = document.getElementById('search');
        const searchValue = search.value;
        let searchArr = [];
        for (let i = 0; i < api.length; i++) {
            let apiData = api[i];
            const desc = apiData.desc;
            if (desc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                searchArr.push({
                    order: apiData.order,
                    desc: apiData.desc,
                    link: apiData.link,
                    alias: apiData.alias,
                    list: apiData.list
                });
            } else {
                let methodList = apiData.list || [];
                let methodListTemp = [];
                for (let j = 0; j < methodList.length; j++) {
                    const methodData = methodList[j];
                    const methodDesc = methodData.desc;
                    if (methodDesc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                        methodListTemp.push(methodData);
                        break;
                    }
                }
                if (methodListTemp.length > 0) {
                    const data = {
                        order: apiData.order,
                        desc: apiData.desc,
                        alias: apiData.alias,
                        link: apiData.link,
                        list: methodListTemp
                    };
                    searchArr.push(data);
                }
            }
        }
        let html;
        if (searchValue === '') {
            const liClass = "";
            const display = "display: none";
            html = buildAccordion(api,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        } else {
            const liClass = "open";
            const display = "display: block";
            html = buildAccordion(searchArr,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        }
        const Accordion = function (el, multiple) {
            this.el = el || {};
            this.multiple = multiple || false;
            const links = this.el.find('.dd');
            links.on('click', {el: this.el, multiple: this.multiple}, this.dropdown);
        };
        Accordion.prototype.dropdown = function (e) {
            const $el = e.data.el;
            let $this = $(this), $next = $this.next();
            $next.slideToggle();
            $this.parent().toggleClass('open');
            if (!e.data.multiple) {
                $el.find('.submenu').not($next).slideUp("20").parent().removeClass('open');
            }
        };
        new Accordion($('#accordion'), false);
    }
}

function buildAccordion(apiData, liClass, display) {
    let html = "";
    if (apiData.length > 0) {
         for (let j = 0; j < apiData.length; j++) {
            html += '<li class="'+liClass+'">';
            html += '<a class="dd" href="#' + apiData[j].alias + '">' + apiData[j].order + '.&nbsp;' + apiData[j].desc + '</a>';
            html += '<ul class="sectlevel2" style="'+display+'">';
            let doc = apiData[j].list;
            for (let m = 0; m < doc.length; m++) {
                html += '<li><a href="#' + doc[m].methodId + '">' + apiData[j].order + '.' + doc[m].order + '.&nbsp;' + doc[m].desc + '</a> </li>';
            }
            html += '</ul>';
            html += '</li>';
        }
    }
    return html;
}